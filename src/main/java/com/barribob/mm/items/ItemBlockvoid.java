package com.barribob.mm.items;

import java.util.List;
import java.util.UUID;

import com.barribob.mm.util.ModUtils;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

/**
 * An items that places blocks at the cost of some durability. It can also mine its block of choice and that replaced durability. To help building, it also increases a player's reach.
 *
 * @author micha
 */
public class ItemBlockvoid extends ItemBase {
    protected final Block block;
    private static final UUID REACH_MODIFIER = UUID.fromString("a6323e02-d8e9-44c6-b941-f5d7155bb406");
    private static final float REACH = 5;
    private float efficiency = 30;

    public ItemBlockvoid(String name, Block block, float efficiency) {
        super(new Item.Properties().durability(1000));
        this.block = block;
        this.efficiency = efficiency;
    }

    // Taken from ItemBlock
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
    	Level worldIn = ctx.getLevel();
    	BlockPos pos = ctx.getClickedPos();
    	Direction facing = ctx.getClickedFace();
        Player player = ctx.getPlayer();
        InteractionHand hand = ctx.getHand();
        Vec3 vec = ctx.getClickLocation();
        BlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!iblockstate.getMaterial().isReplaceable()) {
            pos = pos.relative(facing);
        }

        ItemStack itemstack = player.getItemInHand(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity) null)) {
            BlockState iblockstate1 = this.block.getStateForPlacement(new BlockPlaceContext(ctx));

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, vec.x, vec.y, vec.z, iblockstate1)) {
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.hurtAndBreak(1, player, t -> {
                	t.broadcastBreakEvent(hand);
                });
            }

            return InteractionResult.PASS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    // Taken from ItemBlock
    public boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, Direction side, double x, double y, double z, BlockState newState) {
        if (!world.setBlock(pos, newState, 11))
            return false;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block) {
            this.block.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof ServerPlayer)
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, stack);
        }

        return true;
    }

    // Increase the placement reach of the item
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(REACH_MODIFIER, "Extended Reach Modifier", REACH - 3.0D, Operation.ADDITION));
        }
        return multimap;
    }

    // Only efficient at destroying its own block
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.getBlock() == this.block ? efficiency : super.getDestroySpeed(stack, state);
    }

    // Breaking its own blocks heals its durability
    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isClientSide && state.getBlock() == this.block) {
            stack.hurtAndBreak(-1, entityLiving, t -> {
            	
            });
            return true;
        } else if (entityLiving instanceof Player && state.getBlock() == this.block && stack.getDamageValue() > 0) {
            worldIn.playSound((Player) entityLiving, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 0.15f, 0.3f);
        }

        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc("blockvoid", new ItemStack(this.block).getDisplayName()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
