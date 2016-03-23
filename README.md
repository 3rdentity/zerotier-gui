# zerotier-gui

This is a simple java interface to control a ZeroTier-One network Controller.

It is currently a Work-in-Progress, but it can successfully connect to a self-owned 
Network Controller to create and manafe private networks.

It relies on [edouardswiac/zerotier-api-java](https://github.com/edouardswiac/zerotier-api-java) 
to actually communicate with the Controller.

The GUI is written using JavaFX toolkit and several enhancements, including 
[MigPane](https://github.com/mikaelgrev/miglayout) and [ControlsFX](http://fxexperience.com/controlsfx/).
