client[
    If check(x)
    Then
        server+"left" @+"";
        server!data @!"";
        End
    Else
        server+"right" @+"";
        End
]

|

server [
    client&
    {
        "left": Some(client?query@?""; End)
    }//{
        "right": None
    }
]