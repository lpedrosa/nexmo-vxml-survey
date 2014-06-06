# Stuff I struggled with while integrating API docs

- Voice API response won't tell you which parameters are optional (e.g. error-text is not present if there is no error) this reflects in serialization issues (to map and then get(non-existant-key)
