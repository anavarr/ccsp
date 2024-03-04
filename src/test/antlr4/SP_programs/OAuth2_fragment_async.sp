service[
    If check(x)
    Then
        client+"login" @+ "";
        authenticator & {
            "auth": Some(authenticator?result @?""; End)
        } // { "quit" : None }
    Else
        client+"cancel" @+"";
        End
]
|
client[
    service &
        {"login": Some(authenticator+"password" @+""; authenticator!password @!""; authenticator?data@?""; End)}
        //
        {"cancel": Some(authenticator+"quit" @+"";End)}
]
|
authenticator[
    client &
        {"password": Some(client?password @?""; service+"auth" @+""; service!result @!""; client!data @!""; End)}
        //
        {"quit": None}
]