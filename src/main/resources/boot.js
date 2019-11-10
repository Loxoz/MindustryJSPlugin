/**
 * This is the main boot function
 *  called from Java plugin
 */

const logger = Packages.io.anuke.arc.util.Log;

var debugBoot = {};

function __boot(plugin, engine, classLoader) {
    try {
        debugBoot.plugin = plugin;
        debugBoot.engine = engine;
        debugBoot.classLoader = classLoader;
        logger.info("booted!");
    } catch (ex) {
        try {
            logger.error(ex);
        } catch (ex) {
            __print(ex);
        }
    }
}

let eventList = []
function __onEvent(name, event) {
    for (let i = 0; i < eventList.length; i++) {
        let e = eventList[i];
        if (e.name == name || e.name == "any") {
            e.func(event, name);
        }
    }
}

function __print(message) {
    java.lang.System.out.println(message);
}

/* beta for now */
function registerEvent(name, func) {
    if (typeof name != "string" && typeof func != "function") {
        return false;
    }
    eventList.push({name: name, func: func});
    return eventList.length - 1;
}

function unregisterEvent(id) {
    /* Actually not using: eventList.splice(id, 1); (cuse it's gonna change id of all events above) */
    eventList[id] = null;
}

/* Event register Examples:
-> Log chat messages:
/js registerEvent("PlayerChatEvent", function(event) { __print("Player " + event.player.name + " said: " + event.message) });
/js registerEvent("any", function(event, name) { __print("Event $" + name + " fired!") });
*/
