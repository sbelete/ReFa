library(caret)
library(RSQLite)
library(DBI)
sqlite_test    <- dbDriver("SQLite")
exampledb_test <- dbConnect(sqlite_test,"/Users/paul2/Desktop/cs0320/term-project-as300-dthuku-jryoo-ptouma-sbelete/javacode/predict.sqlite3")
dbListTables(exampledb_test)
query <- sprintf("SELECT * FROM main WHERE link = 'key'")
dbGetQuery(exampledb_test, query)
results_test <- dbSendQuery(exampledb_test, query)

data_test = fetch(results_test,1)
data_test = data_test[ ,c(2:8,13:15)]
data_test$real = as.factor(data_test$real)
data_test$real = make.names(data_test$real)
data_test$c1l1 = as.factor(data_test$c1l1)
data_test$wordpress = as.factor(data_test$wordpress)

model_test <- readRDS("/Users/paul2/Desktop/cs0320/term-project-as300-dthuku-jryoo-ptouma-sbelete/javacode/model.rds")
model_test
prediction <- predict(model_test, newdata = data_test, type = "prob")
real <- prediction[1,1]
