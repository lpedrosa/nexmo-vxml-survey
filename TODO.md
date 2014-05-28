# TODO

## Number Update callback url
Need to update callback url for a specific origin msisdn. That callback url *should be the url of the target survey xml*.

POST /number/update/{api_key}/{api_secret}/{country}/{msisdn}?voiceCallbackType=vxml&voiceCallbackValue=http://localhost:8080/fetch-survey/{survey-id}

Example:

I have got the following surveys:

| Survey ID | Description           |
|   sv-1    | ice-cream flavours    |
|   sv-2    | favourite team sports |

If I want to send a survey using nexmo call api, I need to set up the origin msisdn with the correct voiceCallbackValue url.

for the ice-cream survey - POST /number/update/{api_key}/{api_secret}/{country}/{msisdn}?voiceCallbackType=vxml&voiceCallbackValue=http://localhost:8080/fetch-survey/sv-1
for the favourite team sports survey - POST /number/update/{api_key}/{api_secret}/{country}/{msisdn}?voiceCallbackType=vxml&voiceCallbackValue=http://localhost:8080/fetch-survey/sv-2

## Accept a batch of numbers to send call api requests to them
to start a call you need to do a GET or POST to:

https://rest.nexmo.com/call/json
or
https://rest.nexmo.com/call/xml


POST example

    POST https://rest.nexmo.com/call/json
    body:
    {
        "api_key":"coolstuff",
        "api_secret":"secretstuff",
        "to":"44748005543",
        "from":"one_of_the_numbers_registered_above",
        "error_url":"localhost:8080/survey/error",
        "error_method":"POST",
        "status_url":"localhost:8080/survey/status"
        "status_method":"POST"
    }

This should also work, if I didn't attach any defaul VXML callback to a number.

    POST https://rest.nexmo.com/call/json
    body:
    {
        "api_key":"coolstuff",
        "api_secret":"secretstuff",
        "to":"44748005543",
        "from":"one_of_the_numbers_registered_above",
        "answer_url":"localhost:8080/survey/fetch/sv-1", // this fetches the ice cream flavours survey
        "error_url":"localhost:8080/survey/error",
        "error_method":"POST",
        "status_url":"localhost:8080/survey/status",
        "status_method":"POST"
    }
