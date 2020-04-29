# Focus Library

The library is mostly split into two parts: `storage` and `view`. `storage` is the "raw" view of the underlying data of the OmniFocus database while `view` provides a consistent & current view on that data. 
The reason this difference exists is because the OmniFocus storage may contain multiple transactions which may change the data however they like. Thus, one would need to read all transactions sequentially and merge them before being able to see the _current_ state of the database. Latter of which is what the job of the elements in `view` is.

## Starting simple

To start, we want to open an existing OmniFocus database at `~/Cloud/OmniFocus.ofocus`. For that, we can use `OmniStore` which provides a `fromPath` method:
```kotlin
val path = Paths.get(System.getProperty("user.home"), "Cloud", "OmniFocus.ofocus")
val store = OmniStore.fromPath(path)
```

Through the store we can then take a look at the registered devices:
```kotlin
store.devices.forEach { device ->
    println(device.name)
}
```
We can do similar things with the changesets (what I previously mentioned as "transactions") and capabilities (the database features that are enabled).

However, you may need to be careful as the specified OmniFocus database might be encrypted, in which case you must provide the password before accessing certain things (such as the changesets or contents of changesets). You can check that by checking for the instance being of `EncryptedOmniStorage` and then calling `.providePassword` on it:
```kotlin
if(storage is EncryptedOmniStorage) {
    val password = "" // read password from user
    storage.providePassword(password.toCharArray())
}
```

When it not encrypted (or you've provided the password), you can safely access the changesets:
```kotlin
storage.changeSets.forEach { changeset ->
    println("Changes:" + changeset.container.content.size)
}
```

## Changeset Entries

A changeset entry can be of many things, such as `Alarm`, `Folder`, `Task`, `Perspective` or `Context`. You may notice the absence of a "Project", however that is internally handled as a `Task`, too. Since a changeset describes the _changes_ to the store, the elements inside a changeset are not always complete elements but instead may describe the change of a single property of the specified element. This is reflected in the types of the properties as many are nullable and should not assumed to be present!

## Creating the current view

If you are not interested in the underlying transactions of the store but instead just want to know the current state, you want to use the `OmniFocusState` instead. This provides the current view on the state of the database:
```kotlin
val view = OmniFocusState(storage)
```
(This also requires the storage to be "unlocked", i.e. a password needs to be provided to an encrypted storage. See above for more info)

This view now provides you with the same information you would see in your OmniFocus application. For example, you could list all tasks that exist in the omnifocus database:
```kotlin
view.tasks.forEach { task ->
    println(task.name + " - Finished? " + task.isCompleted)
}
```

Similarly, you can take a look at all the folders and projects.

## Contributions

There are some fields which I may have missed, don't know the format of to provide better implementations (e.g. Enums) or have incorrectly assumed they always exist. If you spot such a field, please create an issue and let me know and it would be great if you could provide some example to follow along.
