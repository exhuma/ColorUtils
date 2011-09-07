ColorUtils
==========

Small java library containing some helper methods to work with color and
accessibility (A11Y). The primary goal for this library was to provide a
method to return a color which is well readable on a given background color.
Or, given a text-color, find a "good" background-color.

Implementation
--------------

The first algorithm implemented is based on a W3C recommendation. As this
recommendation was based on the websafe color palette, the results are not
always satisfactory.

After a bit of research, I found two alternative algorithms based on
Luminosity and Luminosity contrast. LuminosityContrast seemed to yield the
most acceptable results, so this one is implemented by default.

There is also a method to determine whether a color combination is readable or
not. Using this method you can specify the difference algorithm and adapt the
colors appropriately.

Disclaimer
----------

Many parameters are still hardcoded and could be made accessible externally.
But so far I did not yet have the need to change these values dynamically, so
I did not implement them as such.

Example Usage
-------------

::

    import lu.albert.colorutils.ColorA11Y;
    Color textColor = ColorA11Y.getReadableComplement(
        myTextComponent.getBackground());
    myTextComponent.setForeground(textColor);


