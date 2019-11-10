package fr.loxoz.mindustry;

import java.io.InputStreamReader;
import java.util.HashMap;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.anuke.arc.Events;
import io.anuke.arc.util.CommandHandler;
import io.anuke.arc.util.Log;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.game.EventType;
import io.anuke.mindustry.plugin.Plugin;

public class MindustryJSPlugin extends Plugin {

    protected ScriptEngine engine = null;

    public MindustryJSPlugin() {

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            /* JavaScript engine is too old so using the new es6 engine (nashorn) */
            // this.engine = manager.getEngineByName("JavaScript");
            System.setProperty("nashorn.args", "--language=es6");
            this.engine = manager.getEngineByName("nashorn");
            if (this.engine != null) {
                Invocable inv = (Invocable) this.engine;
                InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("boot.js"));
                this.engine.eval(reader);
                inv.invokeFunction("__boot", this, engine, getClass().getClassLoader());
                registerEvents(engine);
            } else {
                Log.err("No JavaScript Engine available. MindustryJSPlugin cannot work without any javascript engine.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.err(ex.getMessage());
        }
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("js", "<code...>", "Run javascript and send result", args -> {
            String code = String.join("", args);
            String result = formatResultToString(runJs(code));
            Log.info("JS> " + result);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {

        handler.<Player>register("js", "<code...>", "Run javascript and send result", (args, player) -> {
            if (player.isAdmin) {
                String code = String.join(" ", args);
                String result = formatResultToString(runJs(code), true);
                player.sendMessage("[gold]" + "JS> " + "[white]" + result);
            }
            else {
                player.sendMessage("[gold]" + "JS> " +"[red]" + "You need to be an admin to run this command!");
            }
        });

    }

    public static void registerEvent(Class<?> eventType, ScriptEngine engine){
        Events.on(eventType, e -> {
            try{
                ((Invocable)engine).invokeFunction("__onEvent", eventType.getSimpleName(), e);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        });
        Log.debug("Registered Event $" + eventType.getSimpleName());
    }

    public static void registerEvents(ScriptEngine engine) {
        Class<?>[] classes = EventType.class.getClasses();
        HashMap<String,Class<?>> events = new HashMap<>();
        for(Class<?> c : classes) events.put(c.getSimpleName(), c);
        events.remove("Trigger");
        events.forEach((name, c) -> registerEvent(c, engine));
    }
    /* Gonna Organize that later (or i'll see) */

    public Object runJs(String code) {
        try {
            return this.engine.eval(code);
        } catch (ScriptException ex) {
            /* ex.printStackTrace(); /* only for debugging in console */
            return (ex.getMessage());
        }
    }

    public String formatResultToString(Object result) {
        return formatResultToString(result, false);
    }

    public String formatResultToString(Object result, Boolean formatted) {
        if (result != null) {
            if (result instanceof String) {
                System.out.println("[Debug] class: " + result.getClass().getName());
                return (formatted ? "[teal]" : "") + "\"" + result.toString() + "\"";
            }
            else if (result instanceof Number) {
                return (formatted ? "[pink]" : "") + result.toString();
            }
            else if (result instanceof Boolean) {
                return (formatted ? "[sky]" : "") + result.toString();
            }
            else if (result.getClass().getName() == "jdk.internal.dynalink.beans.StaticClass") {
                return (formatted ? "[salmon]" : "") + result.toString();
            }
            else if (result.getClass().getName() == "jdk.nashorn.api.scripting.ScriptObjectMirror") {
                return (formatted ? "[cyan]" : "") + result.toString();
            }
            else {
                System.out.println("[Debug] class: " + result.getClass().getName());
                return result.toString();
            }
        }
        else {
            return (formatted ? "[gray]" : "") + "undefined";
        }
    }

}
