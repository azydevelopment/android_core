# android_core
Core Android library that I use for my personal projects. Just a place to put my stuff for now. Hopefully I'll have time to clean it up.

If you want to see some of my code in action, you can download one of my hobby apps, [Readable for Wikipedia™](https://play.google.com/store/apps/details?id=com.arcdatum.apps.readable).

Various things that my library contains that helped me in my Android app development:
* ActivityTwoPane
    * Abstracts away the concept of a two pane tablet mode and one pane mobile mode (with drawer as the side pane) away from the application by taking two fragments (main and side) provided by the app and putting them into their appropriate places.
* AdapterStreamingAsync
    * Uses an LRU cache to enable an arbitrary sized list of dynamically (and asynchronously) loaded objects as 'chunks'.
    * This enables very long lists that would normally have a heavy memory load without sacrificing the ability to quick scroll to any location in the list.  Ie. It decouples the length of the list from the current position of the list from the actual currently loaded data.
* FilterBase
    * This one is the basis for a [Directed Acyclic Graph (DAG)](https://en.wikipedia.org/wiki/Directed_acyclic_graph) of graphics filters.
    * It enables arbitrarily long chains of effects (eg. FilterGaussianBlur) backed by RenderScript.
    * A good number of engineering requirements had to be taken into account here including the need for filters to be marked as 'invalid' so as not to unnecessarily reprocess a node, enable filters that take two inputs (eg. FilterRevealRadial), and more.
    * The code in here is confirmed to support a fairly complex image processing application with a long chain of effects on one of the slowest purchaseable phones while using less than 16MB of working memory (including intermediate surfaces between nodes).
* XmlViewBase
    * Used as the basis of modular view components which can be instantiated both in xml and programmatically.  Ie. Helps when dynamic loading of complex views (stored as xml files) is needed without needing to stuff it into a fragment.
