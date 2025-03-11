p[Call Outer]

Inner:
    q!a @!"";
    q&{
        "l4": Some(q!e1 @!""; End)
    }
    //
    {
        "l5": Some(q!e2 @!""; Call Inner)
    }
    //
    {
        "l6": Some(q!e3 @!""; Call Outer)
    }

Outer:
    q!b @!"";
    q&{
        "l1": Some(q!d1 @!""; End)
    }
    //
    {
        "l2": Some(q!d2 @!""; Call Outer)
    }
    //
    {
        "l3": Some(q!d3 @!""; Call Inner)
    }