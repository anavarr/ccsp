client [ Call Client ]
|
server [ Call Server ]

Client:
    server!data @!"";
    If isValid() Then
        server+"continue"@+"";
        Call Client
    Else
        server+"end"@+"";
        End


Server:
    client?request @?"";
    client&
    {
        "continue" : Some(Call Server)
    }//
    {
        "end" :  None
    }