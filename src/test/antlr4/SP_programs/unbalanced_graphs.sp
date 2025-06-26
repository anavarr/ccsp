p1 [
    If cdt Then
        p2+"left" @+"";
        If cdt Then
            p2+"left"@+"";
            End
        Else
            p2+"right"@+"";
            End
    Else
        p2+"right"@+"";
        End
]
|
p2 [
    p1&{
        "left": Some(p1&{
            "left": None
        }//
        {
            "right": None
        }
    )}//{
        "right": None
    }
]