# commodore-file  [![Javadocs](https://javadoc.io/badge/me.lucko/commodore-file.svg)](https://javadoc.io/doc/me.lucko/commodore-file) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.lucko/commodore-file/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.lucko/commodore-file)

The commodore file format is a simplified way of representing Brigadier command node trees in a string form.

This repository a Java parser for the format.

## Example
Below is an example commodore file for [Minecraft's `/time` command](https://minecraft.gamepedia.com/Commands/time):

```
time {
  set {
    day;
    noon;
    night;
    midnight;
    time brigadier:integer;
  }
  add {
    time brigadier:integer;
  }
  query {
    daytime;
    gametime;
    day;
  }
}
```

To parse to a brigadier `CommandNode`:

```java
LiteralCommandNode<Sender> timeCommand = CommodoreFileReader.INSTANCE.parse(new File("time.commodore"));
```

This will return a command node equivalent to the following node as build with brigadier's API.

```java
LiteralCommandNode<Sender> timeCommand = LiteralArgumentBuilder.literal("time")
        .then(LiteralArgumentBuilder.literal("set")
                .then(LiteralArgumentBuilder.literal("day"))
                .then(LiteralArgumentBuilder.literal("noon"))
                .then(LiteralArgumentBuilder.literal("night"))
                .then(LiteralArgumentBuilder.literal("midnight"))
                .then(RequiredArgumentBuilder.argument("time", IntegerArgumentType.integer())))
        .then(LiteralArgumentBuilder.literal("add")
                .then(RequiredArgumentBuilder.argument("time", IntegerArgumentType.integer())))
        .then(LiteralArgumentBuilder.literal("query")
                .then(LiteralArgumentBuilder.literal("daytime"))
                .then(LiteralArgumentBuilder.literal("gametime"))
                .then(LiteralArgumentBuilder.literal("day"))
        ).build();
```
