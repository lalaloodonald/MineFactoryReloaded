package powercrystals.minefactoryreloaded.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class AutoEnchantmentHelper extends EnchantmentHelper
{
	public static ItemStack addRandomEnchantment(Random rand, ItemStack stack, int level)
	{
		List<EnchantmentData> enchantments = buildEnchantmentList(rand, stack, level);
		boolean isBook = stack.itemID == Item.book.itemID;

		if(isBook)
		{
			stack.itemID = Item.enchantedBook.itemID;
		}

		Collections.shuffle(enchantments);
		if(enchantments != null)
		{
			for(EnchantmentData var6 : enchantments)
			{
				if(isBook)
				{
					Item.enchantedBook.func_92115_a(stack, var6);
					return stack;
				}
				else
				{
					stack.addEnchantment(var6.enchantmentobj, var6.enchantmentLevel);
				}
			}
		}

		return stack;
	}

	public static List<EnchantmentData> buildEnchantmentList(Random rand, ItemStack stack, int level)
	{
		int itemEnchantability = stack.getItem().getItemEnchantability();

		if(itemEnchantability <= 0)
		{
			return null;
		}
		else
		{
			itemEnchantability /= 2;
			itemEnchantability = 1 + rand.nextInt((itemEnchantability >> 1) + 1) + rand.nextInt((itemEnchantability >> 1) + 1);
			int var5 = itemEnchantability + level;
			float var6 = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
			int targetEnchantability = (int)((float)var5 * (1.0F + var6) + 0.5F);

			if(targetEnchantability < 1)
			{
				targetEnchantability = 1;
			}

			List<EnchantmentData> enchantmentList = null;
			Map<Integer, EnchantmentData> enchantmentMap = mapEnchantmentData(targetEnchantability, stack);

			if(enchantmentMap != null && !enchantmentMap.isEmpty())
			{
				EnchantmentData enchData = (EnchantmentData)WeightedRandom.getRandomItem(rand, enchantmentMap.values());

				if(enchData != null)
				{
					enchantmentList = new ArrayList<EnchantmentData>();
					enchantmentList.add(enchData);

					for(int i = targetEnchantability; rand.nextInt(50) <= i; i >>= 1)
					{
						for(Integer enchIndex : enchantmentMap.keySet())
						{
							boolean validWithPreviousEnchants = true;
							for(EnchantmentData newEnchantment : enchantmentList)
							{
								if (newEnchantment.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[enchIndex]))
								{
									continue;
								}

								validWithPreviousEnchants = false;
							}
							if(!validWithPreviousEnchants)
							{
								enchantmentMap.remove(enchIndex);
							}

							break;
						}

						if(!enchantmentMap.isEmpty())
						{
							EnchantmentData randomEnchant = (EnchantmentData)WeightedRandom.getRandomItem(rand, enchantmentMap.values());
							enchantmentList.add(randomEnchant);
						}
					}
				}
			}

			return enchantmentList;
		}
	}
	
	public static Map<Integer, EnchantmentData> mapEnchantmentData(int targetEnchantability, ItemStack stack)
	{
		HashMap<Integer, EnchantmentData> enchantmentMap = null;
		boolean isBook = stack.itemID == Item.book.itemID;

		for(int var7 = 0; var7 < Enchantment.enchantmentsList.length; ++var7)
		{
			Enchantment enchantment = Enchantment.enchantmentsList[var7];

			if(enchantment != null && (enchantment.canApplyAtEnchantingTable(stack) || isBook))
			{
				for(int enchLevel = enchantment.getMinLevel(); enchLevel <= enchantment.getMaxLevel(); ++enchLevel)
				{
					if(targetEnchantability >= enchantment.getMinEnchantability(enchLevel) && targetEnchantability <= enchantment.getMaxEnchantability(enchLevel))
					{
						if(enchantmentMap == null)
						{
							enchantmentMap = new HashMap<Integer, EnchantmentData>();
						}

						enchantmentMap.put(Integer.valueOf(enchantment.effectId), new EnchantmentData(enchantment, enchLevel));
					}
				}
			}
		}

		return enchantmentMap;
	}
}