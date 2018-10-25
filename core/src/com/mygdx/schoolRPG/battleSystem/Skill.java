package com.mygdx.schoolRPG.battleSystem;

import com.mygdx.schoolRPG.World;

import java.util.ArrayList;

public class Skill {

    enum SkillAnimationType {
        NONE,
        MELEE_FIST,
        RANGED_SPIT
    }

    public static SkillAnimationType getSkillAnimationTypeFromString(String str) {
        if (str.equals("melee-fist")) {
            return SkillAnimationType.MELEE_FIST;
        } else if (str.equals("ranged-spit")) {
            return SkillAnimationType.RANGED_SPIT;
        } else {
            return SkillAnimationType.NONE;
        }
    }

    String name;
    int costAP;
    int cooldown;
    boolean positive;
    Unit target;
    ArrayList<DamageType> damageTypes;
    ArrayList<Integer> baseDamages;
    SkillAnimationType skillAnimationType;
    ArrayList<StatusEffect> statusEffects;

    public Skill(World w, String name, int costAP, int maxCooldown, Boolean positive, ArrayList<DamageType> damageTypes, ArrayList<Integer> baseDamages, SkillAnimationType skillAnimationType, ArrayList<StatusEffect> statusEffects) {
        this.name = name;
        this.positive = positive;
        this.costAP = costAP;
        cooldown = maxCooldown;
        this.damageTypes = damageTypes;
        this.baseDamages = baseDamages;
        this.skillAnimationType = skillAnimationType;
        this.statusEffects = statusEffects;
    }
}
