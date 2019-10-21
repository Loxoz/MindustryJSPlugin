/**
 * This is the main boot function
 *  called from Java plugin
 */

const logger = Packages.io.anuke.arc.util.Log; /* supports es6! */

function __boot(plugin, engine, classLoader) {
    try {
        logger.info("booted!");
    } catch (ex) {
        try {
            logger.error(ex);
        } catch (ex) {
            __print(ex);
        }
    }
}

function __print(message) {
    java.lang.System.out.println(message);
}
