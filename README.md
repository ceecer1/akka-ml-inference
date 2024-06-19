
### An example of how to use Deep Java Library [DJL.ai](http://wwww.djl.ai) in Scala's Akka-Http framework

First, download model folders from S3 bucket akka-huggingface-models and copy all three folders into src/main/resources

You can then run inference on huggingface models from:
1. [HuggingFaceQaInference.java](src%2Fmain%2Fscala%2FHuggingFaceQaInference.java)
2. [HuggingFaceBertInference.java](src%2Fmain%2Fscala%2FHuggingFaceBertInference.java)


The endpoint of POST /inferences
```json
{"text":"whatever"} 
```
which shoud compute text embedding and return embedding in string format
```json
{
    "vector": "Array(-0.026074253, -0.08460002, ...,"
}
```

or cURLs
```sh
curl --location --request POST 'http://127.0.0.1:8080/inferences' \
--header 'Content-Type: application/json' \
--data-raw '{"text": "whatever"}'
```


Install SBT, for macOS, 

```sh
brew install sbt
```

Run service:
```sh
sbt run
```

You should see `Server online at http://127.0.0.1:8080/`

Run unit tests:
```sh
sbt test
```

For TensorFlow to optimize on performance:

```
export OMP_NUM_THREADS=1
export TF_NUM_INTEROP_THREADS=1
export TF_NUM_INTRAOP_THREADS=1
```

For more information on optimization, you can check [here](
).




