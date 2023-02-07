package com.gianca1994.aowebbackend.resources.guild.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: Gianca1994
 * Explanation: This class is used to request a guild name.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuildDonateDiamondsDTO {
    private int amountDiamonds;
}
