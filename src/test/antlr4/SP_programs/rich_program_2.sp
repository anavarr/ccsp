server [ Call Server ]

Server:
    #
    client &
        { "/text:GET" :  #GET all the texts
            Some(
                client?req @?"";
                #what about authentication ?
                If isConnected(req.creds)
                Then
                    client!getAllTexts() @!"";
                    Call ProcessStuff
                Else
                    client!sendError() @!"";
                    Call ProcessStuff
            )
        }
        //
        { "/text:POST": #POST new text
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/text/<id>:GET": #GET the text <id>
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/text/<id>:DEL": #DELete the text <id>
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/text/<id>:PUT": #PUT the text <id>
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/author:GET" :  #GET all the texts
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/author:POST": #POST new text
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/author/<id>:GET": #GET the text <id>
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/author/<id>:DEL": #DELete the text <id>
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }
        //
        { "/author/<id>:PUT": #PUT the text <id>
            Some(
                client?req @?"";
                #what about authentication ?
                client!getAllTexts() @!"";
                Call Server
            )
        }