client[
    server?ready @?"";
    If check(x)
    Then
        server+"left" @+"";
        End
    Else
        server+"right" @+"";
        End
]

|

server [
    client!ready @!"";
    client&
    {
        "left": None
    }//{
        "right": None
    }
]