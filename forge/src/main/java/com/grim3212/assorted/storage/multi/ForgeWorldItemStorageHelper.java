package com.grim3212.assorted.storage.multi;

import com.grim3212.assorted.lib.core.inventory.LockedWorldlyContainer;
import com.grim3212.assorted.lib.core.inventory.impl.LockedSidedInvWrapper;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ForgeWorldItemStorageHelper {

    public static LazyOptional<IItemHandlerModifiable>[] create(LockedWorldlyContainer inv, Direction... sides) {
        LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];
        for (int x = 0; x < sides.length; x++) {
            final Direction side = sides[x];
            ret[x] = LazyOptional.of(() -> new ForgeWrappedWorldyItemStorage(new LockedSidedInvWrapper(inv, side)));
        }
        return ret;
    }

}
