p1 [ Call P1 ] | p2 [ Call P2 ]

P1:
    If isValid() Then
        If isValid() Then
            p2+"1" @+"";
            Call P1
        Else
            p2+"2" @+"";
            Call P1
    Else
        If isValid() Then
            p2+"3" @+"";
            Call P1
        Else
            p2+"4" @+"";
            Call P1



P2 :
    p1 &
    { "1": Some(Call P2)}//
    { "2": Some(Call P2)}//
    { "3": Some(Call P2)}//
    { "4": Some(Call P2)}