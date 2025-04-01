client [ Call Client ]

Client:
    If testValid() Then
        If testValid() Then
            server+"choice1" @+"";
            server!data @!"";
            If testValid() Then
                server+"1choice1" @+"";
                Call Client;
            Else
                server+"1choice2" @+"";
                Call Client
        Else
            server+"choice2" @+"";
            If testValid() Then
                server+"2choice1" @+"";
                Call Client
            Else
                server+"2choice2" @+"";
                Call Client
    Else
        server+"end"@+"";
        End