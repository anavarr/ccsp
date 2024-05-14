client [
    server!query @!"";
    If state
    Then
        server&{
            "trueLeft" : Some(server!dataReady @!""; End)
        }//{
            "trueRight" : Some(server?moreData @?""; End)
        }
    Else
        server&{
            "falseLeft" : Some(server!fakeData @!""; End)
        }//{
            "falseRight" : Some(server?moreFakeData @?""; End)
        }
]