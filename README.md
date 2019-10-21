# MindustryJSPlugin

### A Mindustry Plugin to support Javascript scripts as sub-plugins

👉🏻 Please note that this project is in **early alpha**, do not expect to see anything working for now

## Installing

Simply place the `.jar file` you downloaded in the [releases tab](https://github.com/Loxoz/MindustryJSPlugin/releases) or you [built](#building) in your server's `config/plugins` directory and restart the server.
List your currently installed plugins by running the `plugins` command.

## Wiki

*incoming feature*

for `0.1.0 pre` version and above you can do:  
`/js <javascript code>` -> sends result (both player and console can do that for now)

### Example:
![example](images/example.png)
And it supports ES6! (ECMAScript 6)  
See all features: https://github.com/lukehoban/es6features/blob/master/README.md

🚧 Warning: Not all ES6 features are supported like:  
- [Arrows functions & features](https://github.com/lukehoban/es6features/blob/master/README.md#arrows) "`() => {}`" are not supported, so you're gonna have to stick with "`function() {}`"  
- [enhanced object literals](https://github.com/lukehoban/es6features/blob/master/README.md#enhanced-object-literals) "`{ func() {}, myvar, item: "value" }`" doesn't works too  

(haven't tested all yet so some are probably not working too)  
Those requires Java 9 new Nashorn library, assuming that almost everyone is running on Java 8 on their servers i'm sticking with Java 8 (but you can open an Issue and if there is sufficient people complaining, i'm gonna update to Java 9)

## Download

Go to the [releases tab here](https://github.com/Loxoz/MindustryJSPlugin/releases)

## Mindustry Plugin System Note

Please note that the plugin system is in **early alpha**, and is subject to major changes.

## Building

`gradlew jar` / `./gradlew jar`

Output jar should be in `build/libs`.

> #### Hope you ❤️ Enjoy my work, Leave a ⭐️ Star if you found this useful to support me and this project
