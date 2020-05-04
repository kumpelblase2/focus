# Focus

Focus is two things: a cli to interact with OmniFocus databases, and a jvm library that you can use to implement custom functionality in your own program.

This readme will focus on the CLI. You can find another README in the library in this same repository if you'd like to know more about that.

To give a bit of a background as well as a bit of info the usage requirements; I use omnifocus on three different apple devices, however none of which are my "main" work device. Thus, I wanted a way to access (and modify) my omnifocus data directly from this main computer which is running linux. The devices sync via web-dav of nextcloud, giving me access to the store using the nextcloud client on my linux computer. 
The last point is rather significant given that I'm not sure if the other synchronization methods provide any way to access the underlying files.

The goals behind `focus` are thus:
1) provide a cross platform tool to access and modify an omnifocus database
2) provide a querying mechanism to provide filtering mechanics similar to perspectives
3) play well with other tools

## Installation

This library requires Java to be installed, at least Version 8.
To install this library, please download the jar provided under the releases on github. You can place this wherever you like, just make sure the user has access and can execute it there.
You can then simply run: `java -jar focus-full.jar`. Alternatively, you can create an alias or a script to execute this for you. An alias in bash would look like this: `alias focus='java -jar /path/to/focus.jar'`

There are currently no packages available for specific distributions but feel free to create them.

## Usage

To make this small demo a little easier to read, we assume for now we have an unencrypted omnifocus database at ~/Nextcloud/OmniFocus.ofocus.

To start off with one of the core features, lets see what tasks are currently available:
```shell script
$ focus query --location ~/Nextcloud/OmniFocus.ofocus "available"
| ID          | Name                                     | Parent                                   | Contexts | Note       | Due                | Deferred           | Completed          |
+-------------+------------------------------------------+------------------------------------------+----------+------------+--------------------+--------------------+--------------------+
| cbxSSaeJZLS | Add birthdays to calendar                |                                          |          |            |                    |                    |                    |
| cq6yXlN6KY9 | Collect costs for birthday               |                                          |          |            |                    |                    |                    |
| fRW72Nqsxq4 | Create wishlist                          |                                          |          |            |                    |                    |                    |
...
```

To make our lives a little easier, we can save the storage location (and more) in the config. For now, we'll just save the location:
```shell script
$ focus create-config ~/Nextcloud/OmniFocus.ofocus
```

And now we can continue without having to specify the location!

If we want to get some information about our registered devices, we can run the following:
```shell script
$ focus devices list
Device fz8qRwcVJgx: MyiPad (J320AP) Last Sync: 2020-04-28T08:06:05Z
Device kI6--3m3aY2: MyMacbook (MacBookAir7,2) Last Sync: 2020-04-25T15:56:17Z
Device isjNQ8kdkMr: MyiPhone (D211AP) Last Sync: 2020-04-28T14:30:22Z
```

If we want to also change stuff, we need to create a new device, so we play nice with omnifocus. To do that, we can do the following:
```shell script
$ focus device register "Focus CLI"
Registered device 'Focus CLI' with ID 'lg9C146kGbH'
```

Which both created the device in the omnifocus store and marked it to be used as the device for modifications to the store.


## Contribution & Help

While I have decent sample data to work with as well as being able to debug using my own devices, I may still miss certain cases or configurations. In that case please file an issue here and I'll try to get it fixed.
