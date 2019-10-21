package fr.loxoz.mindustry;

import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.anuke.arc.util.CommandHandler;
import io.anuke.arc.util.Log;
import io.anuke.mindustry.entities.type.Player;
import io.anuke.mindustry.plugin.Plugin;

public class MindustryJSPlugin extends Plugin {

    protected ScriptEngine engine = null;

    public MindustryJSPlugin() {

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            this.engine = manager.getEngineByName("JavaScript");
            if (this.engine != null) {
                Invocable inv = (Invocable) this.engine;
                this.engine.eval(new InputStreamReader(utils.getResource("engineboot.js")));
                inv.invokeFunction("boot", this, engine);
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
        if (result instanceof String) {
            return (formatted ? "[teal]" : "" ) + "\"" + result.toString() + "\"";
        }
        else if (result instanceof Number) {
            return (formatted ? "[pink]" : "" ) + result.toString();
        }
        else if (result instanceof Boolean) {
            return (formatted ? "[sky]" : "" ) + result.toString();
        }
        else {
            return result.toString();
        }
    }

}
