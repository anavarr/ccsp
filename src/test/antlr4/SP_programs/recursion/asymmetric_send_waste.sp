client [ Call Client ] | server [ Call Server ]

Client:
    If isValid() Then
        server+"continue" @+"";
        server!data @!"";
        Call Client
    Else
        server+"quit" @+"";
        End

Server:
    client&{
        "continue": Some(client?res @?""; End)
    } // {
        "quit": None
    }