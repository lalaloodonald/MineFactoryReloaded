package powercrystals.minefactoryreloaded.decorative;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFactoryRoad extends Block
{
	public BlockFactoryRoad(int id)
	{
		super(id, Material.rock);
		blockIndexInTexture = 12;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta)
	{
		if(meta == 0) return 12;
		if(meta == 1 || meta == 3) return 13;
		if(meta == 2 || meta == 4) return 14;
		return 12;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		float gap = 0.125F;
		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + 1, y + 1 - gap, z + 1);
	}

	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		par5Entity.motionX *= 1.5D;
		par5Entity.motionZ *= 1.5D;
	}
	
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId)
	{
		if(!world.isRemote)
		{
			int meta = world.getBlockMetadata(x, y, z);
			if((meta == 1 || meta == 3) && world.isBlockIndirectlyGettingPowered(x, y, z))
			{
				world.setBlockMetadataWithNotify(x, y, z, meta + 1);
			}
			else if((meta == 2 || meta == 4) && !world.isBlockIndirectlyGettingPowered(x, y, z))
			{
				world.setBlockMetadataWithNotify(x, y, z, meta - 1);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getBlockBrightness(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 2 || meta == 4 ? 1 : 0;
	}
	
	@Override
	public String getTextureFile()
	{
		return MineFactoryReloadedCore.terrainTexture;
	}
}