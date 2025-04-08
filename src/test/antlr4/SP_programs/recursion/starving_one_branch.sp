starver [ Call Starver ]
|
starved [ Call Starved ]

Starved:
    starver &
    { "continue" : Some(Call Starved) }
    //
    { "quit" : None }

Starver:
    starved + "continue" @+"";
    Call Starver