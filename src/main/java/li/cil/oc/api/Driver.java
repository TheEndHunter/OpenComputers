package li.cil.oc.api;

import li.cil.oc.api.driver.*;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * This API allows registering new drivers with the mod.
 * <p/>
 * Drivers are used to make items and third-party blocks available in the mod's
 * component network, and optionally to user programs. If you implement a new
 * block that should interact with the mod's component network it is enough to
 * have it implement {@link Environment} - no driver is
 * needed in that case.
 * <p/>
 * Note that these methods should <em>not</em> be called in the pre-init phase,
 * since the {@link li.cil.oc.api.API#driver} may not have been initialized
 * at that time. Only start calling these methods in the init phase or later.
 *
 * @see Network
 * @see DriverBlock
 * @see DriverItem
 */
public final class Driver {
    /**
     * Registers a new side-aware block driver.
     * <p/>
     * Whenever the neighboring blocks of an Adapter block change, it checks if
     * there exists a driver for the changed block, and if it is configured to
     * interface that block type connects it to the component network.
     * <p/>
     * This must be called in the init phase, <em>not</em> the pre- or post-init
     * phases.
     *
     * @param driver the driver to register.
     */
    public static void add(final DriverBlock driver) {
        if (API.driver != null)
            API.driver.add(driver);
    }

    /**
     * Registers a new item driver.
     * <p/>
     * Item components can inserted into a computers component slots. They have
     * to specify their type, to determine into which slots they can fit.
     * <p/>
     * This must be called in the init phase, <em>not</em> the pre- or post-init
     * phases.
     *
     * @param driver the driver to register.
     */
    public static void add(final DriverItem driver) {
        if (API.driver != null)
            API.driver.add(driver);
    }

    /**
     * Registers a new type converter.
     * <p/>
     * Type converters are used to automatically convert values returned from
     * callbacks to a "simple" format that can be pushed to any architecture.
     * <p/>
     * This must be called in the init phase, <em>not</em> the pre- or post-init
     * phases.
     *
     * @param converter the converter to register.
     */
    public static void add(final Converter converter) {
        if (API.driver != null)
            API.driver.add(converter);
    }

    /**
     * Register a new environment provider.
     * <p/>
     * Environment providers are used for mapping item stacks to the type of
     * environment that will be created by the stack, either by it being
     * placed in the world and acting as a block component, or by being
     * placed in an component inventory and created by the item's driver.
     *
     * @param provider the provider to register.
     */
    public static void add(final EnvironmentProvider provider) {
        if (API.driver != null)
            API.driver.add(provider);
    }

    /**
     * Register a new inventory provider.
     * <p/>
     * Inventory providers are used for accessing item inventories using
     * the inventory controller upgrade, for example.
     *
     * @param provider the provider to register.
     */
    public static void add(final InventoryProvider provider) {
        if (API.driver != null)
            API.driver.add(provider);
    }

    /**
     * Looks up a driver for the block at the specified position in the
     * specified world.
     * <p/>
     * Note that several drivers for a single block can exist. Because of this
     * block drivers are always encapsulated in a 'compound' driver, which is
     * what will be returned here. In other words, you should will <em>not</em>
     * get actual instances of drivers registered via {@link #add(li.cil.oc.api.driver.Block)}.
     *
     * @param world the world containing the block.
     * @param pos   the position of the block.
     * @return a driver for the block, or <tt>null</tt> if there is none.
     */
    @Nullable
    public static DriverBlock driverFor(final World world, final BlockPos pos, final EnumFacing side) {
        if (API.driver != null)
            return API.driver.driverFor(world, pos, side);
        return null;
    }

    /**
     * Looks up a driver for the specified item stack.
     * <p/>
     * Note that unlike for blocks, there can always only be one item driver
     * per item. If there are multiple ones, the first one that was registered
     * will be used.
     *
     * @param stack the item stack to get a driver for.
     * @param host  the type that will host the environment created by returned driver.
     * @return a driver for the item, or <tt>null</tt> if there is none.
     */
    @Nullable
    public static DriverItem driverFor(final ItemStack stack, final Class<? extends EnvironmentHost> host) {
        if (API.driver != null)
            return API.driver.driverFor(stack, host);
        return null;
    }

    /**
     * Looks up a driver for the specified item stack.
     * <p/>
     * Note that unlike for blocks, there can always only be one item driver
     * per item. If there are multiple ones, the first one that was registered
     * will be used.
     * <p/>
     * This is a context-agnostic variant used mostly for "house-keeping"
     * stuff, such as querying slot types and tier.
     *
     * @param stack the item stack to get a driver for.
     * @return a driver for the item, or <tt>null</tt> if there is none.
     */
    @Nullable
    public static DriverItem driverFor(final ItemStack stack) {
        if (API.driver != null)
            return API.driver.driverFor(stack);
        return null;
    }

    /**
     * Looks up the environments associated with the specified item stack.
     * <p/>
     * This will use the registered {@link EnvironmentProvider}s to find
     * environment types for the specified item stack. If none can be
     * found, returns an empty Set.
     *
     * @param stack the item stack to get the environment type for.
     * @return the type of environment associated with the stack, or an empty Set, or null if the API is not present.
     */
    public static Set<Class<?>> environmentsFor(final ItemStack stack) {
        if (API.driver != null)
            return API.driver.environmentsFor(stack);
        return Collections.emptySet();
    }

    /**
     * Get an IItemHandler implementation providing access to an item inventory.
     * <p/>
     * This will use the registered {@link InventoryProvider}s to find an
     * IItemHandler implementation providing access to the specified stack.
     * If none can be found, returns <tt>null</tt>.
     * <p/>
     * Note that the specified <tt>player</tt> may be null, but will usually
     * be the <em>fake player</em> of the agent making use of this API.
     *
     * @param stack  the item stack to get the inventory access for.
     * @param player the player holding the item. May be <tt>null</tt>.
     * @return the IItemHandler implementation interfacing the stack, or <tt>null</tt>.
     */
    @Nullable
    public static IItemHandler itemHandlerFor(final ItemStack stack, final EntityPlayer player) {
        if (API.driver != null)
            return API.driver.itemHandlerFor(stack, player);
        return null;
    }

    /**
     * Get a list of all registered block drivers.
     * <p/>
     * This is intended to allow checking for particular drivers using more
     * customized logic.
     * <p/>
     * The returned collection is read-only.
     *
     * @return the list of all registered block drivers.
     */
    public static Collection<DriverBlock> blockDrivers() {
        if (API.driver != null)
            return API.driver.blockDrivers();
        return Collections.emptyList();
    }

    /**
     * Get a list of all registered item drivers.
     * <p/>
     * This is intended to allow checking for particular drivers using more
     * customized logic.
     * <p/>
     * The returned collection is read-only.
     *
     * @return the list of all registered item drivers.
     */
    public static Collection<DriverItem> itemDrivers() {
        if (API.driver != null)
            return API.driver.itemDrivers();
        return Collections.emptyList();
    }

    // ----------------------------------------------------------------------- //

    private Driver() {
    }
}
