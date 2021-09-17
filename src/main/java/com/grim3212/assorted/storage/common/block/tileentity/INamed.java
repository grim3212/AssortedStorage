package com.grim3212.assorted.storage.common.block.tileentity;

import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;

public interface INamed extends INameable {

	public void setCustomName(ITextComponent name);
}
