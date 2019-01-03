package com.mygdx.schoolRPG.battleSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.XmlReader;
import com.mygdx.schoolRPG.battleSystem.ui.UnitsDrawGroup;
import org.w3c.dom.Element;
import com.mygdx.schoolRPG.Particle;
import com.mygdx.schoolRPG.World;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.GlobalSequence;

import java.util.ArrayList;

public class Skill {

    enum SkillAnimationType {
        NONE,
        MELEE_FIST,
        RANGED_SPIT,
        MAGIC_AURA
    }

    public static SkillAnimationType getSkillAnimationTypeFromString(String str) {
        if (str.equals("melee-fist")) {
            return SkillAnimationType.MELEE_FIST;
        } else if (str.equals("ranged-spit")) {
            return SkillAnimationType.RANGED_SPIT;
        } else if (str.equals("magic-aura")) {
            return SkillAnimationType.MAGIC_AURA;
        } else {
            return SkillAnimationType.NONE;
        }
    }

    String name;
    ArrayList<String> title;
    int costAP;
    public int cooldown;
    int baseHeal;
    boolean healPercent;
    int maxCooldown;
    boolean positive;
    ArrayList<DamageType> damageTypes;
    ArrayList<Integer> baseDamages;
    SkillAnimationType skillAnimationType;
    ArrayList<StatusEffect> statusEffects;

    Color color;
    String prtName;

    String texInfo;
    AnimationSequence texFront, texBack;
    Sound hitSound;

    float appearAlpha = 0, hitOffset = 0, hitSpeed = 0;
    Texture additionalTex;
    boolean spawned = false;
    public boolean finished = false;
    ArrayList<Particle> curPrts;

    public Skill(World w, String name, ArrayList<String> title, int costAP, int maxCooldown, Boolean positive, ArrayList<DamageType> damageTypes, ArrayList<Integer> baseDamages, String baseHeal, SkillAnimationType skillAnimationType, ArrayList<StatusEffect> statusEffects, org.w3c.dom.Element animInfo) {
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
        healPercent = baseHeal.endsWith("%");
        if (healPercent) this.baseHeal = Integer.parseInt(baseHeal.substring(0, baseHeal.length()-1));
        else this.baseHeal = Integer.parseInt(baseHeal);
        String soundName = animInfo.getAttribute("sound");
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            int fps = Integer.parseInt(animInfo.getAttribute("fps"));
            boolean looping = Boolean.parseBoolean(animInfo.getAttribute("looping"));
            int frames = Integer.parseInt(animInfo.getAttribute("frames"));
            this.texBack = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get(w.worldDir.path() + "/skills/" + animInfo.getAttribute("name") + "_back.png", Texture.class), frames), fps, looping);
            this.texFront = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get(w.worldDir.path() + "/skills/" + animInfo.getAttribute("name") + "_front.png", Texture.class), frames), fps, looping);
        } else if (skillAnimationType == SkillAnimationType.MAGIC_AURA) {
            int fps = Integer.parseInt(animInfo.getAttribute("fps"));
            boolean looping = Boolean.parseBoolean(animInfo.getAttribute("looping"));
            int frames = Integer.parseInt(animInfo.getAttribute("frames"));
            this.texFront = new AnimationSequence(new GlobalSequence(w.assets, w.assets.get(w.worldDir.path() + "/skills/" + animInfo.getAttribute("name") + ".png", Texture.class), frames), fps, looping);
            color = w.battleSystem.hex2Rgb(animInfo.getAttribute("color"));
            prtName = animInfo.getAttribute("prt");
            curPrts = new ArrayList<Particle>();
            additionalTex = w.assets.get(Gdx.files.internal("aura.png").path(), Texture.class);
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
        appearAlpha = 0;
        spawned = false;
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            hitOffset = 0;
            hitSpeed = 0;
        } else if (skillAnimationType == SkillAnimationType.RANGED_SPIT) {

        } else if (skillAnimationType == SkillAnimationType.MAGIC_AURA) {

        }
    }

    private void doStuff(World w, UnitsDrawGroup udg, Unit target, Unit attacker, float curUnitX, float curUnitY) {
        if (hitSound != null) hitSound.play(w.menu.soundVolume / 100.0f);
        if (baseDamages.size() > 0) {
            target.playHitSound(w);
            ArrayList<Integer> actualDamages = new ArrayList<Integer>();
            for (int i = 0; i < baseDamages.size(); ++i) {
                actualDamages.add(baseDamages.get(i) + attacker.stats.str);
            }
            boolean deadPrev = target.stats.dead;
            target.hit(udg, damageTypes, actualDamages);
            if (!deadPrev && target.stats.dead) {
                udg.battle.addExpToPool(target, target.stats.baseExpReward);
            }
        }
        if (baseHeal > 0) {
            target.heal(w, baseHeal, healPercent, udg, curUnitX - texFront.getFirstFrame().getRegionWidth(), curUnitY + udg.layerDist/2.0f);
        }
    }

    public void predraw(World w, UnitsDrawGroup udg, SpriteBatch batch, Unit attacker, Unit target, boolean attacking, float curUnitX, float curUnitY, float scale) {
        if (skillAnimationType == SkillAnimationType.MAGIC_AURA) {
            int r;
            if (attacking) r = 2 * ((int)attacker.getWidth() / 2 + 10);
            else r = 2 * ((int)target.getWidth() / 2 + 10);

            batch.setColor(new Color(color.r, color.g, color.b, appearAlpha * 0.5f));
            for (int i = -r; i <= r; i+=2) {
                batch.draw(additionalTex, curUnitX + i, curUnitY + scale * (r / 6.0f) * (float) Math.sin(Math.acos((float) i / (float) r)), 2, additionalTex.getHeight() * 2);
            }
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        }
    }

    public void draw(World w, UnitsDrawGroup udg, SpriteBatch batch, Unit attacker, Unit target, boolean attacking, float curUnitX, float curUnitY, float scale) {
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
                        doStuff(w, udg, target, attacker, curUnitX, curUnitY);
                    }
                } else {
                    hitOffset += hitSpeed;
                    appearAlpha -= 0.02f;
                }
                if (appearAlpha <= 0) {
                    finished = true;
                }
            }
        } else if (skillAnimationType == SkillAnimationType.MAGIC_AURA) {
            if (!attacking)
            {
                if (appearAlpha < 1.0 && !spawned) {
                    appearAlpha += 0.02f;
                } else if (appearAlpha >= 1.0f && !spawned) {
                    //spawn
                    for (int j = 0; j < 10; ++j) {
                        Particle p = new Particle(w.assets, texFront.getFirstFrame().getTexture(), false, curUnitX - texFront.getFirstFrame().getRegionWidth(), curUnitY + udg.layerDist/2.0f, 1.0f);
                        p.setAlphaStep(0, 0.01f);
                        udg.addParticle(p);
                    }
                    doStuff(w, udg, target, attacker, curUnitX, curUnitY);
                    spawned = true;
                } else {
                    appearAlpha -= 0.02f;
                }
                if (appearAlpha <= 0) {
                    finished = true;
                }
            } else {
                batch.setColor(new Color(1.0f, 1.0f, 1.0f, appearAlpha));
                batch.draw(texFront.getCurrentFrame(false), curUnitX - texFront.getFirstFrame().getRegionWidth(), curUnitY + target.getHeight() + 10 + texFront.getFirstFrame().getRegionHeight(), texFront.getFirstFrame().getRegionWidth() * 2.0f, texFront.getFirstFrame().getRegionHeight() * 2.0f);
                batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
            }
        }
    }

    public void afterdraw(World w, UnitsDrawGroup udg, SpriteBatch batch, Unit attacker, Unit target, boolean attacking, float curUnitX, float curUnitY, float scale) {
        if (skillAnimationType == SkillAnimationType.MELEE_FIST) {
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, appearAlpha));
            if (attacking) {
                batch.draw(texFront.getCurrentFrame(false), curUnitX - texFront.getFirstFrame().getRegionWidth(), curUnitY - hitOffset * 32 * scale - texFront.getFirstFrame().getRegionHeight()/2.0f, texFront.getFirstFrame().getRegionWidth() * 2.0f, texFront.getFirstFrame().getRegionHeight() * 2.0f);
            } else {
                batch.draw(texBack.getCurrentFrame(false), curUnitX - texBack.getFirstFrame().getRegionWidth(), curUnitY + hitOffset * 32 * scale - texFront.getFirstFrame().getRegionHeight()/2.0f - target.getHeight()/2.0f, texBack.getFirstFrame().getRegionWidth() * 2.0f, texBack.getFirstFrame().getRegionHeight() * 2.0f);
            }
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        } else if (skillAnimationType == SkillAnimationType.MAGIC_AURA) {
            int r;
            if (attacking) r = 2 * ((int)attacker.getWidth() / 2 + 10);
            else r = 2 * ((int)target.getWidth() / 2 + 10);

            batch.setColor(new Color(color.r, color.g, color.b, appearAlpha * 0.5f));
            for (int i = -r; i <= r; i+=2) {
                batch.draw(additionalTex, curUnitX + i, curUnitY - scale * (r/6.0f) * (float)Math.sin(Math.acos((float)i / (float) r)), 2, additionalTex.getHeight() * 2);
            }
            batch.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        }
    }
}
