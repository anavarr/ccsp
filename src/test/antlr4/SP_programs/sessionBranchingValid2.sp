client [
    If myConditionIsTrue()
    Then
        server+"success" @+"";
        proxy+"success" @+"";
        proxy2+"success" @+"";

        server!myRequest @!"";
        proxy!myData @!"";
        server?res @?"";
        proxy?myCorrectedData @?"";
        End
    Else
        server+"failure" @+"";
        proxy+"failure" @+"";
        proxy2+"failure" @+"";

        proxy2!notifyFailure() @!"";
        End
]