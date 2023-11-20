ip [ Call X_IP ] | client [ Call X_Client ] | server [ Call X_Server ]

X_IP:
    client?x @? "";
    If check(x)
    Then
        client+"left" @+"";
        server+"left" @+"";
        client!token @!"";
        Call Y_IP
    Else
        client+"right" @+"";
        server+"right" @+"";
        Call X_IP

X_Client:
    ip!req @!"";
    ip &
        { "left":
            Some(
                ip?x @?"";
                server!x @!"";
                Call Y_Client
            )
        }
        //
        {"right":
            Some(
                Call X_Client
            )
        }

X_Server:
    ip &
        { "left":
            Some(
                client?x @? "";
                Call Y_Server
            )
        }
        //
        { "right":
            Some(
                Call X_Server
            )
        }