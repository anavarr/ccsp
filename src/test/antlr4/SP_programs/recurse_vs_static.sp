p1 [ Call P1 ] | p2 [ Call P2 ]

P1:
    p2 & {
        "continue": Some(
            Call P1
        )
    }//
    {
        "end": None
    }

P2:
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "continue" @+"";
    p1 + "end" @+"";
    End
