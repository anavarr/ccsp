client [ Call Client ]

Client :
server?id @?"";
If check(x)
Then
    server&
    {"get" : Some(
        If check(y)
        Then
            server+"password1" @+"";
            End
        Else
            server+"password2" @+"";
            End
    )}
    //
    {"post": Some(
        If check(y)
        Then
            server+"password3" @+"";
            End
        Else
            server+"password4" @+"";
            End
    )}
    //
    {"del": Some(
        If check(y)
        Then
            server+"password5" @+"";
            End
        Else
            server+"password6" @+"";
            End
    )}

Else
    server&
        {"gadjo" : Some(
            If check(y)
            Then
                server+"data1" @+"";
                End
            Else
                server+"data2" @+"";
                End
        )}
        //
        {"gadji": Some(
            If check(y)
            Then
                server+"data3" @+"";
                End
            Else
                server+"data4" @+"";
                End
        )}