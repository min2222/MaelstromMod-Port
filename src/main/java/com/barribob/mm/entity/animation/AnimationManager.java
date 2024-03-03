package com.barribob.mm.entity.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Manages animations of all entities. All that needs to be done is that it should be added to the entity to start the animation. Animations get automatically removed when the entity is removed or
 * dies.
 *
 * @author Barribob
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class AnimationManager {
    private static Map<LivingEntity, Map<String, BBAnimation>> animations = new HashMap<>();
    private static Map<Model, Map<ModelPart, float[]>> defaultModelValues = new HashMap<>();
    private static Map<LivingEntity, Set<String>> animationsToRemoveOnceEnded = new HashMap<>();

    public static void updateAnimation(LivingEntity entity, String animationId, boolean remove) {
        if (remove) {
            removeAnimation(entity, animationId);
            return;
        }

        if (!animations.containsKey(entity)) {
            animations.put(entity, new HashMap<>());
        }

        if (!animations.get(entity).containsKey(animationId)) {
            animations.get(entity).put(animationId, new BBAnimation(animationId));
        }

        animations.get(entity).get(animationId).startAnimation();
    }

    private static void removeAnimation(LivingEntity entity, String animationId) {
        if (animations.containsKey(entity)) {
            if (animations.get(entity).containsKey(animationId)) {
                BBAnimation animation = animations.get(entity).get(animationId);
                if(animation.isLoop()) {
                    scheduleLoopingAnimationStop(entity, animationId);
                }
                else {
                    animations.get(entity).remove(animationId);
                }
            }
        }
    }

    private static void scheduleLoopingAnimationStop(LivingEntity entity, String animationId) {
        if(!animationsToRemoveOnceEnded.containsKey(entity)) {
            animationsToRemoveOnceEnded.put(entity, new HashSet<>());
        }

        animationsToRemoveOnceEnded.get(entity).add(animationId);
    }

    private static void removeEndedSheduledEndedLoopingAnimations(LivingEntity entity, Map<String, BBAnimation> animations, float partialTicks) {
        if(animationsToRemoveOnceEnded.containsKey(entity)) {
            for (String animationId : animationsToRemoveOnceEnded.get(entity)) {
                BBAnimation animation = animations.get(animationId);
                if(animation != null && animation.isLoop() && animation.isAtAnimationEnd(partialTicks)) {
                    animations.remove(animationId);
                    animationsToRemoveOnceEnded.get(entity).remove(animationId);
                }
            }
        }
    }

    /**
     * Receive periodic updates to looping animation in case the animation is destroyed under certain conditions If the animation exists, it will not be updated
     */
    public static void updateLoopingAnimation(LivingEntity entity, String animationId) {
        if (!animations.containsKey(entity)) {
            animations.put(entity, new HashMap<>());
        }

        if (!animations.get(entity).containsKey(animationId)) {
            animations.get(entity).put(animationId, new BBAnimation(animationId));
        }
    }

    /**
     * Update the animations of every entity one tick forward
     *
     * @param event
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END && !Minecraft.getInstance().isPaused()) {
            List<LivingEntity> entitiesToRemove = new ArrayList<LivingEntity>();
            for (Entry<LivingEntity, Map<String, BBAnimation>> entry : animations.entrySet()) {
                LivingEntity entity = entry.getKey();

                if (!entity.isAddedToWorld() && entity.isDeadOrDying()) {
                    entitiesToRemove.add(entity);
                    continue;
                }

                // Pause animation on death
                if (entity.getHealth() <= 0) {
                    continue;
                }

                List<String> animsToRemove = new ArrayList<String>();
                Map<String, BBAnimation> animationMap = entry.getValue();
                for (Entry<String, BBAnimation> kv : animationMap.entrySet()) {
                    if (kv.getValue().isEnded()) {
                        animsToRemove.add(kv.getKey());
                    } else {
                        kv.getValue().update();
                    }
                }

                // Remove ended animations
                for (String id : animsToRemove) {
                    animationMap.remove(id);
                }
            }

            // Remove entities not in this world anymore
            for (LivingEntity entity : entitiesToRemove) {
                animations.remove(entity);
            }
        }
    }

    /**
     * This has to be separate from the animation below because we want to add in passive head and arm animations in the model itself.
     *
     * @param model
     */
    public static void resetModel(Model model) {
        /**
         * This part solves an issue that comes from the fact that all instances of a particular entity share the same model and thus will each alter the models values. Those values can carry over to the next
         * entity who uses the model, so those values have to be reset before each entity get rendered with the model.
         */
        if (defaultModelValues.containsKey(model)) {
            for (ModelPart renderer : model.boxList) {
                float[] values = defaultModelValues.get(model).get(renderer);
                renderer.xRot = values[0];
                renderer.yRot = values[1];
                renderer.zRot = values[2];
                renderer.offsetX = values[3];
                renderer.offsetY = values[4];
                renderer.offsetZ = values[5];
                renderer.rotationPointX = values[6];
                renderer.rotationPointY = values[7];
                renderer.rotationPointZ = values[8];
            }
        } else {
            defaultModelValues.put(model, new HashMap<ModelPart, float[]>());
            for (ModelPart renderer : model.boxList) {
                defaultModelValues.get(model).put(renderer, new float[]{
                        renderer.xRot,
                        renderer.yRot,
                        renderer.zRot,
                        renderer.offsetX,
                        renderer.offsetY,
                        renderer.offsetZ,
                        renderer.rotationPointX,
                        renderer.rotationPointY,
                        renderer.rotationPointZ,
                });
            }
        }
    }

    public static void setModelRotations(Model model, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        // Update all models for each entity
        if (animations.containsKey(entity)) {
            for (BBAnimation animation : animations.get(entity).values()) {
                animation.setModelRotations(model, limbSwing, limbSwingAmount, entity.getHealth() <= 0 ? 0 : partialTicks);
            }
            removeEndedSheduledEndedLoopingAnimations(entity, animations.get(entity), partialTicks);
        }
    }
}
