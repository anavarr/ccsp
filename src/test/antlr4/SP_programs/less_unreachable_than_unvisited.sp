p1 [ Call P1 ]
|
p2 [ Call P2 ]

P1:
    If isValid() Then
        p2+"label1" @+""; End
    Else
        p2+"label2" @+""; End

P2:
    p1&
    { "label1": None}
    //
    { "label2": None}
    //
    { "label3": None}
    //
    { "label4": None}
    //
    { "label5": None}