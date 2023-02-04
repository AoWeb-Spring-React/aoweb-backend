package com.gianca1994.aowebbackend.resources.user;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gianca1994.aowebbackend.exception.Conflict;
import com.gianca1994.aowebbackend.resources.jwt.JwtTokenUtil;
import com.gianca1994.aowebbackend.resources.user.dto.queyModel.UserAttributes;
import com.gianca1994.aowebbackend.resources.user.dto.request.NameRequestDTO;
import com.gianca1994.aowebbackend.resources.user.dto.response.RankingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @Author: Gianca1994
 * Explanation: UserController
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STANDARD')")
    public User getProfile(@RequestHeader(value = "Authorization") String token) throws Conflict {
        /**
         * @Author: Gianca1994
         * Explanation: This function is in charge of getting the profile of the user.
         * @param String token
         * @return User user
         */
        try {
            return userService.getProfile(
                    jwtTokenUtil.getUsernameFromToken(token.substring(7))
            );
        } catch (Exception e) {
            throw new Conflict("Error in getting the profile");
        }
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STANDARD')")
    public RankingResponseDTO getRankingAll(@RequestParam("page") int page) throws Conflict {
        /**
         * @Author: Gianca1994
         * Explanation: This method is used to get the ranking of all the users.
         * @return ArrayList<User> users
         */
        try {
            return userService.getRankingAll(page);
        } catch (Exception e) {
            throw new Conflict("Error in getting the ranking");
        }
    }

    @PostMapping("/add-skill-points/{skillName}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STANDARD')")
    public UserAttributes setFreeSkillPoint(@RequestHeader(value = "Authorization") String token,
                                            @PathVariable String skillName) throws Conflict {
        /**
         * @Author: Gianca1994
         * Explanation: This method is used to add skill points to the user.
         * @param String token
         * @param FreeSkillPointDTO freeSkillPointDTO
         * @return User user
         */
        try {
            return userService.setFreeSkillPoint(
                    jwtTokenUtil.getIdFromToken(token.substring(7)), skillName
            );
        } catch (Exception e) {
            throw new Conflict("Error in adding skill points");
        }
    }

    @PostMapping("/attack-user")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STANDARD')")
    public ArrayList<ObjectNode> attackUser(@RequestHeader(value = "Authorization") String token,
                                            @RequestBody NameRequestDTO nameRequestDTO) throws Conflict {
        /**
         * @Author: Gianca1994
         * Explanation: This method is used to attack another user.
         * @param String token
         * @param NameRequestDTO nameRequestDTO
         * @return ArrayList<ObjectNode> objectNodes
         */
        try {
            return userService.userVsUserCombatSystem(
                    jwtTokenUtil.getUsernameFromToken(token.substring(7)), nameRequestDTO
            );
        } catch (Exception e) {
            throw new Conflict("Error in attacking the user");
        }
    }

    @PostMapping("/attack-npc")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STANDARD')")
    public ArrayList<ObjectNode> attackNpc(@RequestHeader(value = "Authorization") String token,
                                           @RequestBody NameRequestDTO nameRequestDTO) throws Conflict {
        /**
         * @Author: Gianca1994
         * Explanation: This method is used to attack a npc.
         * @param String token
         * @param NameRequestDTO nameRequestDTO
         * @return ArrayList<ObjectNode> objectNodes
         */
        try {
            return userService.userVsNpcCombatSystem(
                    jwtTokenUtil.getUsernameFromToken(token.substring(7)), nameRequestDTO
            );
        } catch (Exception e) {
            throw new Conflict("Error in attacking the npc");
        }
    }
}
