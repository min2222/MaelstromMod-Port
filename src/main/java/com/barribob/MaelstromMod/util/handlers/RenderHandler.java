package com.barribob.MaelstromMod.util.handlers;

import com.barribob.MaelstromMod.entity.entities.EntityAzureGolem;
import com.barribob.MaelstromMod.entity.entities.EntityAzureVillager;
import com.barribob.MaelstromMod.entity.entities.EntityBeast;
import com.barribob.MaelstromMod.entity.entities.EntityDreamElk;
import com.barribob.MaelstromMod.entity.entities.EntityFloatingSkull;
import com.barribob.MaelstromMod.entity.entities.EntityHerobrineOne;
import com.barribob.MaelstromMod.entity.entities.EntityHorror;
import com.barribob.MaelstromMod.entity.entities.EntityMaelstromIllager;
import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMage;
import com.barribob.MaelstromMod.entity.entities.EntityShade;
import com.barribob.MaelstromMod.entity.model.ModelAnimatedBiped;
import com.barribob.MaelstromMod.entity.model.ModelBeast;
import com.barribob.MaelstromMod.entity.model.ModelDreamElk;
import com.barribob.MaelstromMod.entity.model.ModelFloatingSkull;
import com.barribob.MaelstromMod.entity.model.ModelHorror;
import com.barribob.MaelstromMod.entity.model.ModelMaelstromMage;
import com.barribob.MaelstromMod.entity.model.ModelShade;
import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.projectile.ProjectileBullet;
import com.barribob.MaelstromMod.entity.render.RenderAnimatedBiped;
import com.barribob.MaelstromMod.entity.render.RenderAzureGolem;
import com.barribob.MaelstromMod.entity.render.RenderAzureVillager;
import com.barribob.MaelstromMod.entity.render.RenderMaelstromIllager;
import com.barribob.MaelstromMod.entity.render.RenderModEntity;
import com.barribob.MaelstromMod.entity.render.RenderProjectile;
import com.barribob.MaelstromMod.entity.util.EntityPortalSpawn;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler
{
    public static void registerEntityRenderers()
    {
	registerModEntityRenderer(EntityShade.class, new ModelShade(), new ResourceLocation(Reference.MOD_ID + ":textures/entity/shade.png"));
	registerModEntityRenderer(EntityHorror.class, new ModelHorror(), new ResourceLocation(Reference.MOD_ID + ":textures/entity/horror.png"));
	registerModEntityRenderer(EntityDreamElk.class, new ModelDreamElk(), new ResourceLocation(Reference.MOD_ID + ":textures/entity/dream_elk.png"));
	registerModEntityRenderer(EntityBeast.class, new ModelBeast(), new ResourceLocation(Reference.MOD_ID + ":textures/entity/beast.png"));
	registerModEntityRenderer(EntityMaelstromMage.class, new ModelMaelstromMage(), new ResourceLocation(Reference.MOD_ID + ":textures/entity/shade.png"));
	registerModEntityRenderer(EntityFloatingSkull.class, new ModelFloatingSkull(), new ResourceLocation(Reference.MOD_ID + ":textures/entity/floating_skull.png"));
	registerModBipedRenderer(EntityHerobrineOne.class, new ModelAnimatedBiped(),
		new ResourceLocation(Reference.MOD_ID + ":textures/entity/herobrine_1.png"), 0.5f);

	registerProjectileRenderer(Projectile.class, ModItems.INVISIBLE);
	registerProjectileRenderer(ProjectileBullet.class, ModItems.IRON_PELLET);
	registerProjectileRenderer(EntityPortalSpawn.class, ModItems.INVISIBLE);

	RenderingRegistry.registerEntityRenderingHandler(EntityAzureVillager.class, new IRenderFactory<EntityAzureVillager>()
	{
	    @Override
	    public Render<? super EntityAzureVillager> createRenderFor(RenderManager manager)
	    {
		return new RenderAzureVillager(manager);
	    }
	});

	RenderingRegistry.registerEntityRenderingHandler(EntityMaelstromIllager.class, new IRenderFactory<EntityMaelstromIllager>()
	{
	    @Override
	    public Render<? super EntityMaelstromIllager> createRenderFor(RenderManager manager)
	    {
		return new RenderMaelstromIllager(manager);
	    }
	});

	RenderingRegistry.registerEntityRenderingHandler(EntityAzureGolem.class, new IRenderFactory<EntityAzureGolem>()
	{
	    @Override
	    public Render<? super EntityAzureGolem> createRenderFor(RenderManager manager)
	    {
		return new RenderAzureGolem(manager, new ResourceLocation(Reference.MOD_ID + ":textures/entity/azure_golem.png"));
	    }
	});
    }

    /**
     * Registers an entity with a model and sets it up for rendering
     */
    private static <T extends EntityLiving, U extends ModelBase, V extends RenderModEntity> void registerModEntityRenderer(Class<T> entityClass, U model,
	    ResourceLocation textures)
    {
	RenderingRegistry.registerEntityRenderingHandler(entityClass, new IRenderFactory<T>()
	{
	    @Override
	    public Render<? super T> createRenderFor(RenderManager manager)
	    {
		return new RenderModEntity(manager, textures, model);
	    }
	});
    }

    /**
     * Registers a biped to render
     */
    private static <T extends EntityLiving, U extends ModelAnimatedBiped, V extends RenderModEntity> void registerModBipedRenderer(Class<T> entityClass, U model,
	    ResourceLocation textures, float shadowSize)
    {
	RenderingRegistry.registerEntityRenderingHandler(entityClass, new IRenderFactory<T>()
	{
	    @Override
	    public Render<? super T> createRenderFor(RenderManager manager)
	    {
		return new RenderAnimatedBiped(manager, model, shadowSize, textures);
	    }
	});
    }

    /**
     * Makes a projectile render with the given item
     * 
     * @param projectileClass
     */
    private static <T extends Entity> void registerProjectileRenderer(Class<T> projectileClass, Item item)
    {
	RenderingRegistry.registerEntityRenderingHandler(projectileClass, new IRenderFactory<T>()
	{
	    @Override
	    public Render<? super T> createRenderFor(RenderManager manager)
	    {
		return new RenderProjectile(manager, Minecraft.getMinecraft().getRenderItem(), item);
	    }
	});
    }
}
