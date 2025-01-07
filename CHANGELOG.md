# Changelog for `DreamHouse-Bot`

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Added

Bot stuff:

- Moderation commands:
    - ban: Takes a user and a reason as parameters. The ban is **permanent**.
    - kick: Takes a user and a reason as parameters.
    - mute: Takes a user, an amount of time (the bot supports a lot of time units) and a reason as parameters. The mute
      is a discord timeout.
    - purge: Delete messages. Takes a number of messages in parameters.
- A working role assignment by message reactions system which is depending on what roles comes after an emoji in a
  message
  in the role channel
- Bot configurations

More technical stuff:

- A way to run the bot from a shadow jar
- An error handling utility wrapper function

### Removed

- Message commands

The explanations for this are quite long and I who's writing in this changelog right now, Maya, have had a hard time
with the libraries I am using ([DiscordKt](https://github.com/DiscordKt/DiscordKt) and therefore
also [Kord](https://github.com/kordlib/kord)). The way it handles message commands was simply not something I like or
was able to deal with really so I decided to simply just not include them. Are they just not a *pain* to remember
anyway?

**I will then focus on** creating complete **slash commands** if possible.

## 0.2.0 - 2025-01-07

### Added

- Creation of the project with the library DiscordKt/DiscordKt from GitHub
- Added test features:
    - A "hello world command"
    - A sketch of a choose-your-roles with reactions on a message system

## 0.1.0 - 2024-12-15