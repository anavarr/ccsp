client[
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
    client&
    {
        "left": None
    }//{
        "right": None
    }
]