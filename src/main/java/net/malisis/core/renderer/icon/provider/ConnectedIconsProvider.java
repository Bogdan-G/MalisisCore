/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.core.renderer.icon.provider;

import static net.minecraft.util.EnumFacing.*;
import net.malisis.core.renderer.icon.MalisisIcon;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * @author Ordinastie
 *
 */
public class ConnectedIconsProvider implements IBlockIconProvider
{
	private static int NONE = 0;
	private static int LEFT = 1;
	private static int TOP = 1 << 1;
	private static int RIGHT = 1 << 2;
	private static int BOTTOM = 1 << 3;
	private static int FULL = LEFT | TOP | RIGHT | BOTTOM;

	//@formatter:off
	public static EnumFacing[][] sides = { 	{ WEST, NORTH, EAST, SOUTH },
											{ WEST, NORTH, EAST, SOUTH },
											{ EAST, UP, WEST, DOWN },
											{ WEST, UP, EAST, DOWN },
											{ NORTH, UP, SOUTH, DOWN },
											{ SOUTH, UP, NORTH, DOWN }};
	//@formatter:on

	private MalisisIcon part1;
	private MalisisIcon part2;
	private MalisisIcon[] icons = new MalisisIcon[16];
	private boolean initialized = false;

	public ConnectedIconsProvider(String name)
	{
		part1 = new MalisisIcon(name);
		part2 = new MalisisIcon(name + "2");
	}

	@Override
	public void registerIcons(TextureMap map)
	{
		part1.register(map);
		part2.register(map);
	}

	protected void initializeIcons()
	{
		float f = 1F / 3F;

		icons[LEFT | TOP] = part1.copy().clip(0, 0, f, f);
		icons[TOP] = part1.copy().clip(f, 0, f, f);
		icons[RIGHT | TOP] = part1.copy().clip(2 * f, 0, f, f);

		icons[LEFT] = part1.copy().clip(0, f, f, f);
		icons[NONE] = part1.copy().clip(f, f, f, f);
		icons[RIGHT] = part1.copy().clip(2 * f, f, f, f);

		icons[LEFT | BOTTOM] = part1.copy().clip(0, 2 * f, f, f);
		icons[BOTTOM] = part1.copy().clip(f, 2 * f, f, f);
		icons[RIGHT | BOTTOM] = part1.copy().clip(2 * f, 2 * f, f, f);

		icons[LEFT | TOP | BOTTOM] = part2.copy().clip(0, 0, f, f);
		icons[TOP | BOTTOM] = part2.copy().clip(f, 0, f, f);
		icons[LEFT | RIGHT | TOP] = part2.copy().clip(2 * f, 0, f, f);

		icons[LEFT | RIGHT] = part2.copy().clip(0, f, f, f);
		icons[FULL] = part2.copy().clip(f, f, f, f);
		//icons[LEFT | RIGHT] = icon.copy().clip(2 * f, f, f, f);

		icons[LEFT | RIGHT | BOTTOM] = part2.copy().clip(0, 2 * f, f, f);
		//icons[TOP | BOTTOM] = icon.copy().clip(f, 2 * f, f, f);
		icons[RIGHT | TOP | BOTTOM] = part2.copy().clip(2 * f, 2 * f, f, f);

		initialized = true;
	}

	/**
	 * Gets the {@link IIcon} with no connection on any side.
	 *
	 * @return the full icon
	 */
	@Override
	public MalisisIcon getIcon()
	{
		if (!initialized)
			initializeIcons();

		return icons[FULL];
	}

	/**
	 * Gets the corresponding {@link IIcon} based on the connections available.
	 *
	 * @param world the world
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param side the side
	 * @return the icon
	 */
	@Override
	public MalisisIcon getIcon(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing)
	{
		if (!initialized)
			initializeIcons();
		int connections = getConnections(world, pos, facing);
		return icons[connections];
	}

	/**
	 * Determines the connections available at this position for the specified <b>side</b>.
	 *
	 * @param world the world
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param side the side
	 * @return the connections
	 */
	private int getConnections(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		Block block = world.getBlockState(pos).getBlock();
		int connection = 0;
		for (int i = 0; i < 4; i++)
		{
			if (world.getBlockState(pos.offset(sides[facing.getIndex()][i])).getBlock() == block)
				connection |= (1 << i);
		}
		return ~connection & 15;
	}
}