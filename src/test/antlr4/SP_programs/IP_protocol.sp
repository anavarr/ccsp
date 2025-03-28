ip [ Call X_IP ] | client [ Call X_Client ] | server [ Call X_Server ]

X_IP:
    client?x @? "";
    If check(x)
    Then
        client+"left" @+"";
        server+"left" @+"";
        client!token @!"";
        End
    Else
        client+"right" @+"";
        server+"right" @+"";
        Call X_IP

Y_IP:
    End

X_Client:
    ip!req @!"";
    ip &
        { "left":
            Some(
                ip?x @?"";
                server!x @!"";
                End
            )
        }
        //
        {"right":
            Some(
                Call X_Client
            )
        }

Y_Client: End

X_Server:
    ip &
        { "left":
            Some(
                client?x @? "";
                End
            )
        }
        //
        { "right":
            Some(
                Call X_Server
            )
        }

Y_Server: End