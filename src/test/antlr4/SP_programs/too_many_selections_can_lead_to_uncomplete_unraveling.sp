client [ Call Client ]

Client:
    If testValid() Then
        If testValid() Then
            server+"choice1" @+"";
            server!data @!"";
            If testValid() Then
                server+"1choice1" @+"";
                If testValid() Then
                    server+"test1" @+"";
                    Call Client
                Else
                    server+"test2" @+"";
                    Call Client
            Else
                server+"1choice2" @+"";
                If testValid() Then
                    server+"test3" @+"";
                    Call Client
                Else
                    server+"test4" @+"";
                    Call Client
        Else
            server+"choice2" @+"";
            If testValid() Then
                If testValid() Then
                    server+"test5" @+"";
                    Call Client
                Else
                    server+"test6" @+"";
                    Call Client
            Else
                If testValid() Then
                    server+"test7" @+"";
                    Call Client
                Else
                    server+"test8" @+"";
                    Call Client
    Else
        If testValid() Then
            server+"choice3" @+"";
            If testValid() Then
                If testValid() Then
                    server+"test9" @+"";
                    Call Client
                Else
                    server+"test10" @+"";
                    Call Client
            Else
                If testValid() Then
                    server+"test11" @+"";
                    Call Client
                Else
                    server+"test12" @+"";
                    Call Client
        Else
            server+"choice4" @+"";
            If testValid() Then
                If testValid() Then
                    server+"test13" @+"";
                    Call Client
                Else
                    server+"test14" @+"";
                    Call Client
            Else
                If testValid() Then
                    server+"test15" @+"";
                    Call Client
                Else
                    server+"test16" @+"";
                    End
