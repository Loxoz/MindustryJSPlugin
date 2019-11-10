# MindustryJSPlugin

### A Mindustry Plugin to support Javascript scripts as sub-plugins

👉🏻 Please note that this project is in **early alpha**, do not expect to see anything working for now, and it's subject to major changes  

> PRs are welcome ;)

## Installing

Simply place the `.jar file` you downloaded in the [releases tab](https://github.com/Loxoz/MindustryJSPlugin/releases) or you [built](#building) in your server's `config/mods` directory and restart the server.
List your currently installed plugins by running the `mods` command.

## Wiki

*coming feature*

for `0.1.0 pre` version and above you can do:  
`/js <javascript code>` -> sends result (both player and console can do that for now)

### Example:
![example](images/example.png)  
🚧 Warning: It has a little support of **ES6** fow now (ECMAScript 6)  
➡ Actually only `const` and `let` are supported  
See all ES6 features: https://github.com/lukehoban/es6features/blob/master/README.md

I'm still looking for solutions like running the new https://www.graalvm.org/

## Download

Go to the [releases tab here](https://github.com/Loxoz/MindustryJSPlugin/releases)

## Building

`gradlew jar` / `./gradlew jar`

Output jar should be in `build/libs`.

> #### Hope you ❤️ Enjoy my work, Leave a ⭐️ Star if you found this useful to support me and this project
