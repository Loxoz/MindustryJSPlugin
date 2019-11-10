package fr.loxoz.mindustry;

import java.io.InputStreamReader;

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
    }

    public static void registerEvents(ScriptEngine engine) {
        registerEvent(EventType.BlockBuildBeginEvent.class, engine);
        registerEvent(EventType.BlockBuildEndEvent.class, engine);
        registerEvent(EventType.BlockDestroyEvent.class, engine);
        registerEvent(EventType.BlockInfoEvent.class, engine);
        registerEvent(EventType.BuildSelectEvent.class, engine);
        registerEvent(EventType.ClientLoadEvent.class, engine);
        registerEvent(EventType.CommandIssueEvent.class, engine);
        registerEvent(EventType.CoreItemDeliverEvent.class, engine);
        registerEvent(EventType.DepositEvent.class, engine);
        registerEvent(EventType.DisposeEvent.class, engine);
        registerEvent(EventType.GameOverEvent.class, engine);
        registerEvent(EventType.LaunchEvent.class, engine);
        registerEvent(EventType.LineConfirmEvent.class, engine);
        registerEvent(EventType.LoseEvent.class, engine);
        registerEvent(EventType.MapMakeEvent.class, engine);
        registerEvent(EventType.MapPublishEvent.class, engine);
        registerEvent(EventType.MechChangeEvent.class, engine);
        registerEvent(EventType.PlayEvent.class, engine);
        registerEvent(EventType.PlayerChatEvent.class, engine);
        registerEvent(EventType.PlayerConnect.class, engine);
        registerEvent(EventType.PlayerJoin.class, engine);
        registerEvent(EventType.PlayerLeave.class, engine);
        registerEvent(EventType.ResearchEvent.class, engine);
        registerEvent(EventType.ResetEvent.class, engine);
        registerEvent(EventType.ResizeEvent.class, engine);
        registerEvent(EventType.StateChangeEvent.class, engine);
        registerEvent(EventType.TileChangeEvent.class, engine);
        registerEvent(EventType.TurretAmmoDeliverEvent.class, engine);
        registerEvent(EventType.UnitCreateEvent.class, engine);
        registerEvent(EventType.UnitDestroyEvent.class, engine);
        registerEvent(EventType.UnlockEvent.class, engine);
        registerEvent(EventType.WaveEvent.class, engine);
        registerEvent(EventType.WinEvent.class, engine);
        registerEvent(EventType.WithdrawEvent.class, engine);
        registerEvent(EventType.WorldLoadEvent.class, engine);
        registerEvent(EventType.ZoneConfigureCompleteEvent.class, engine);
        registerEvent(EventType.ZoneRequireCompleteEvent.class, engine);
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
