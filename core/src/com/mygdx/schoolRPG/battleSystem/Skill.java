package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.World;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.GlobalSequence;

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
    ArrayList<String> title;
    int costAP;
    public int cooldown;
    int maxCooldown;
    boolean positive;
    ArrayList<DamageType> damageTypes;
    ArrayList<Integer> baseDamages;
    SkillAnimationType skillAnimationType;
    ArrayList<StatusEffect> statusEffects;

    String texInfo;
    AnimationSequence texFront, texBack;
    Sound hitSound;

    float appearAlpha = 0, hitOffset = 0, hitSpeed = 0;
    public boolean finished = false;
    ArrayList<Particle> curPrts;

    public Skill(World w, String name, ArrayList<String> title, int costAP, int maxCooldown, Boolean positive, ArrayList<DamageType> damageTypes, ArrayList<Integer> baseDamages, SkillAnimationType skillAnimationType, ArrayList<StatusEffect> statusEffects, String texInfo, int fps, boolean looping, int frames, String soundName) {
        this.name = name;
        this.title = title;
        this.positive = positive;
        this.costAP = costAP;
        cooldown = 0;
        this.maxCooldown = maxCooldown;
        this.damageTypes = damageTypes;
        this.baseDamages = baseDamages;
        this.skillAnimationType = skillAnimationType;
        this.statusEffects = statusEffects;
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            this.texBack = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get(w.worldDir.path() + "/skills/" + texInfo + "_back.png", Texture.class), frames), fps, looping);
            this.texFront = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get(w.worldDir.path() + "/skills/" + texInfo + "_front.png", Texture.class), frames), fps, looping);
        }
        if (!soundName.equals("")) hitSound = w.assets.get(w.worldDir.path() + "/skills/" + soundName, Sound.class);
    }

    public Skill(Skill s) {
        this.name = s.name;
        this.title = s.title;
        this.positive = s.positive;
        this.costAP = s.costAP;
        cooldown = 0;
        this.maxCooldown = s.maxCooldown;
        this.damageTypes = s.damageTypes;
        this.baseDamages = s.baseDamages;
        this.skillAnimationType = s.skillAnimationType;
        this.statusEffects = s.statusEffects;
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            this.texBack = s.texBack;
            this.texFront = s.texFront;
        }
        this.hitSound = s.hitSound;
    }

    public void turnUpdate()
    {
        if (cooldown > 0) cooldown--;
        resetAnimation();
    }

    public void resetAnimation() {
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            hitOffset = 0;
            appearAlpha = 0;
            hitSpeed = 0;
        } else if (skillAnimationType == SkillAnimationType.RANGED_SPIT) {

        }
    }

    public void draw(World w, SpriteBatch batch, Unit attacker, Unit target, boolean attacking, float curUnitX, float curUnitY, float scale) {
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            if (!attacking)
            {
                if (appearAlpha < 1.0 && hitSpeed >= 0) {
                    appearAlpha += 0.02f;
                } else if (hitSpeed >= 0) {
                    hitOffset += hitSpeed;
                    hitSpeed += 0.02f;
                    if (hitOffset >= 1.0f) {
                        hitOffset = 1.0f;
                        hitSpeed = -0.02f;
                        if (hitSound != null) hitSound.play(w.menu.soundVolume / 100.0f);
                        target.playHitSound(w);
                        ArrayList<Integer> actualDamages = new ArrayList<Integer>();
                        for (int i = 0; i < baseDamages.size(); ++i) {
                            actualDamages.add(baseDamages.get(i) + attacker.stats.str);
                        }
                        target.hit(damageTypes, actualDamages);
                    }
                } else {
                    hitOffset += hitSpeed;
                    appearAlpha -= 0.02f;
                }
                if (appearAlpha <= 0) {
                    finished = true;
                }
            }
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, appearAlpha));
            if (attacking) {
                batch.draw(texFront.getCurrentFrame(false), curUnitX - texFront.getFirstFrame().getRegionWidth(), curUnitY - hitOffset * 32 * scale - texFront.getFirstFrame().getRegionHeight()/2.0f, texFront.getFirstFrame().getRegionWidth() * 2.0f, texFront.getFirstFrame().getRegionHeight() * 2.0f);
            } else {
                batch.draw(texBack.getCurrentFrame(false), curUnitX - texBack.getFirstFrame().getRegionWidth(), curUnitY + hitOffset * 32 * scale - texFront.getFirstFrame().getRegionHeight()/2.0f, texBack.getFirstFrame().getRegionWidth() * 2.0f, texBack.getFirstFrame().getRegionHeight() * 2.0f);
            }
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        }
    }
}
