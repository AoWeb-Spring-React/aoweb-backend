package com.gianca1994.aowebbackend.service;

import java.util.ArrayList;
import java.util.Comparator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gianca1994.aowebbackend.model.Npc;
import com.gianca1994.aowebbackend.model.User;
import com.gianca1994.aowebbackend.pvpSystem.PveUserVsNpc;
import com.gianca1994.aowebbackend.repository.NpcRepository;
import com.gianca1994.aowebbackend.repository.RoleRepository;
import com.gianca1994.aowebbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    NpcRepository npcRepository;

    PveUserVsNpc pveUserVsNpc = new PveUserVsNpc();

    public User getProfile(String username) {
        /**
         * @Author: Gianca1994
         * Explanation: This function is in charge of getting the profile of the user.
         * @param String username
         * @return User
         */
        return userRepository.findByUsername(username);
    }

    public ArrayList<User> getRankingAll() {
        /**
         * @Author: Gianca1994
         * Explanation: This function is in charge of getting the ranking of all users.
         * @param none
         * @return ArrayList<User>
         */
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        users.sort(Comparator.comparing(User::getLevel).reversed());
        return users;
    }

    //////////////////////////////////////////////////////////////////////
    ////////////// INFO: Abajo, lo relacionado al PVP y PVE //////////////
    //////////////////////////////////////////////////////////////////////

    public ArrayList<ObjectNode> pvpUserVsUser(String usernameAttacker, String usernameDefender) {
        User attacker = userRepository.findByUsername(usernameAttacker);
        User defender = userRepository.findByUsername(usernameDefender);

        if (attacker == null || defender == null) return null;

        if (attacker.getHp() < attacker.getMaxHp() * 0.25f ||
                defender.getHp() < defender.getMaxHp() * 0.25f) {
            return null;
        }

        ArrayList<ObjectNode> historyCombat = new ArrayList<>();

        int roundCounter = 0;
        boolean stopPvP = false;
        do {
            roundCounter++;
            int attackerDmg = (int) ((int) (Math.random() * (attacker.getMaxDmg() - attacker.getMinDmg())) + attacker.getMinDmg());
            int defenderDmg = (int) ((int) (Math.random() * (defender.getMaxDmg() - defender.getMinDmg())) + defender.getMinDmg());

            if (!stopPvP) {
                defender.setHp(defender.getHp() - attackerDmg);
                if (defender.getHp() <= 0) {
                    defender.setHp(0);
                    long goldTheft = (long) (defender.getGold() * 0.25f);
                    attacker.setGold(attacker.getGold() + goldTheft);
                    defender.setGold(defender.getGold() - goldTheft);
                    stopPvP = true;
                } else {
                    attacker.setHp(attacker.getHp() - defenderDmg);
                    if (attacker.getHp() <= 0) {
                        attacker.setHp(0);
                        attacker.setGold((long) (attacker.getGold() - (attacker.getGold() * 0.1f)));
                        stopPvP = true;
                    }
                }
            }

            ObjectNode round = new ObjectMapper().createObjectNode();
            round.put("round", roundCounter);
            round.put("attackerLife", attacker.getHp());
            round.put("defenderLife", defender.getHp());
            round.put("attackerDmg", attackerDmg);
            round.put("defenderDmg", defenderDmg);
            historyCombat.add(round);


        } while (attacker.getHp() > 0 && defender.getHp() > 0);

        userRepository.save(attacker);
        userRepository.save(defender);

        return historyCombat;
    }


    public ArrayList<ObjectNode> userVsNpcCombatSystem(String username, long npcId) {
        /**
         * @Author: Gianca1994
         * Explanation: This function is in charge of the combat between the user and the npc.
         * @param String usernameAttacker
         * @param long npcId
         * @return ArrayList<ObjectNode>
         */

        User user = userRepository.findByUsername(username);

        if (user == null) return null;
        if (pveUserVsNpc.checkLifeStartCombat(user)) return null;

        Npc npc = npcRepository.findById(npcId).get();
        ArrayList<ObjectNode> historyCombat = new ArrayList<>();

        int roundCounter = 0;
        boolean stopPvP = false;

        do {
            roundCounter++;
            int userDmg = pveUserVsNpc.calculateUserDmg(user);
            int npcDmg = pveUserVsNpc.calculateNpcDmg(npc);

            // El pvp concluyo?
            if (!stopPvP) {
                npc.setHp(npc.getHp() - userDmg);

                // El npc murio?
                if (pveUserVsNpc.checkIfNpcDied(npc)) {
                    npc.setHp(0);
                    user.setExperience(pveUserVsNpc.CalculateUserExperienceGain(user, npc));
                    user.setGold(pveUserVsNpc.CalculateUserGoldGain(user, npc));

                    // Usuario sube de nivel?
                    if (pveUserVsNpc.checkUserLevelUp(user)) {
                        user.setLevel(pveUserVsNpc.userLevelUp(user));
                        user.setExperienceToNextLevel(pveUserVsNpc.userLevelUpNewNextExpToLevel(user));
                    }
                    stopPvP = true;
                } else {
                    user.setHp(user.getHp() - npcDmg);
                    if (pveUserVsNpc.checkIfUserDied(user)) {
                        user.setHp(0);
                        stopPvP = true;
                    }
                }
            }

            historyCombat.add(pveUserVsNpc.roundJsonGeneratorUserVsNpc(
                    user, npc, roundCounter, userDmg, npcDmg)
            );

        } while (user.getHp() > 0 && npc.getHp() > 0);

        npc.setHp(npc.getMaxHp());
        userRepository.save(user);

        return historyCombat;
    }
}
