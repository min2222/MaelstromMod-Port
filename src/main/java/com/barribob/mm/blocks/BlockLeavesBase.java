package com.barribob.mm.blocks;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.IHasModel;

/**
 * A lot of these methods come from the BlockOldLeaves class to make the leaf
 * decay functionality work with my leaves
 */
public class BlockLeavesBase extends LeavesBlock implements IHasModel {
    public BlockLeavesBase(String name) {
        super();
        setUnlocalizedName(name);
        setRegistryName(name);

        // Set fancy graphics to true for these leaves
        Main.proxy.setFancyGraphics(this, true);

        // Adds states so that we can use the BlockLeaves decaying feature
        setDefaultState(blockState.getBaseState().withProperty(CHECK_DECAY, Boolean.valueOf(true))
                .withProperty(DECAYABLE, Boolean.valueOf(true)));

        // Add both an item as a block and the block itself
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new BlockItem(this).setRegistryName(this.getRegistryName()));
    }

    public BlockLeavesBase(String name, float hardness, float resistance, SoundType soundType) {
        this(name);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(soundType);
    }

    /**
     * Helper function called from the client proxy
     *
     * @param isFancy
     */
    public void setFancyGraphics(boolean isFancy) {
        this.setGraphicsLevel(isFancy);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, BlockGetter world, BlockPos pos, int fortune) {
        return new ArrayList<ItemStack>();
    }

    @Override
    public EnumType getWoodType(int meta) {
        return null;
    }

    @Override
    protected StateDefinition createBlockState() {
        return new StateDefinition(this, new IProperty[]{CHECK_DECAY, DECAYABLE});
    }

    @Override
    public int getMetaFromState(BlockState state) {
        int i = 0;

        if (!((Boolean) state.getValue(DECAYABLE)).booleanValue()) {
            i |= 4;
        }

        if (((Boolean) state.getValue(CHECK_DECAY)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0))
                .withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
    }
}
