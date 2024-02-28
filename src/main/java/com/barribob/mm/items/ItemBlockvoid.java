package com.barribob.mm.items;

import java.util.List;
import java.util.UUID;

import com.barribob.mm.util.ModUtils;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

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
    public InteractionResult onItemUse(Player player, Level worldIn, BlockPos pos, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ) {
        BlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity) null)) {
            int i = this.getMetadata(itemstack.getMetadata());
            BlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1)) {
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.damageItem(1, player);
            }

            return EnumActionResult.PASS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    // Taken from ItemBlock
    public boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, BlockState newState) {
        if (!world.setBlockState(pos, newState, 11))
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
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(Player.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER, "Extended Reach Modifier", REACH - 3.0D, 0).setSaved(false));
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
    public boolean onBlockDestroyed(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isRemote && state.getBlock() == this.block) {
            stack.damageItem(-1, entityLiving);
            return true;
        } else if (entityLiving instanceof Player && state.getBlock() == this.block && stack.getItemDamage() > 0) {
            worldIn.playSound((Player) entityLiving, pos, SoundEvents.ENDEREYE_DEATH, SoundSource.BLOCKS, 0.15f, 0.3f);
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("blockvoid", new ItemStack(this.block).getDisplayName()));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
