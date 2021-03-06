import React, { Component } from 'react';
import api from "./services/api";
import "./Research.css"

let optVector = []

class Research extends Component {
    state = {
        pages: "",
        pageIndex: 0,
        questions: [],
        answers: [],
        isLoaded: false,
        answeredPage: [],
    }

    //Carrega as perguntas e respostas da pesquisa
    componentDidMount() {
        this.getResearchData()
    }

    //Carrega as perguntas e respostas do BD para as pesquisas
    async getResearchData() {
        const questions = []
        const answers = []
        const response = await api.get("/api/dados/estrutura-basica")
        // console.log(response)

        response.data.perguntasERespostas.map((item, i) => {
            questions.push(item.pergunta)
            answers.push(item.respostas)
        })
        this.setState({
            pages: response.data.folhas,
            questions: questions,
            answers: answers,
            isLoaded: true,
        })
    }

    //Preenche uma estrutura auxiliar no formato da requisição, com as respostas do usuário
    fillAnsweredPage = (questao, opcao) => {
        let obj = {}
        if (optVector.length === 0) {
            obj = {
                "folha": this.state.pages[this.state.pageIndex].numero,
                "opcaoResposta": opcao,
                "pergunta": this.state.questions[questao].numero,
                "pesquisa": this.state.pages[this.state.pageIndex].pesquisa.id
            }
            optVector.push(obj)
        } else {

            optVector.map((item, id) => {
                if (this.state.questions[questao].numero == item.pergunta) {
                    let remove = optVector.splice(id, 1)
                }
            })

            obj = {
                "folha": this.state.pages[this.state.pageIndex].numero,
                "opcaoResposta": opcao,
                "pergunta": this.state.questions[questao].numero,
                "pesquisa": this.state.pages[this.state.pageIndex].pesquisa.id
            }
            optVector.push(obj)

        }

        // console.log(optVector)
    }

    //Mostra as respostas possíveis para cada pergunta
    showAnswers = (id) => {
        let answersObj = this.state.answers[id]
        return (
            <div>
                {answersObj.map((options, idA) => {
                    // console.log(options)
                    return (
                        <div id={`Radio_${this.state.pageIndex}`}>
                            <input type="radio" value={options.opcao} name = {`input_${id}`}
                            onClick = {() => this.fillAnsweredPage(id, options.opcao)}
                            />
                            <label >{options.descricao}</label>
                        </div>
                    )
                })}
            </div>
        )
    }

    //Mostras as perguntas
    showQuestions = () => {
        // console.log(this.state.questions)
        return this.state.questions.map((questionObj, id) => {
            return (
                <div className="Questions">
                    <h4 className="QNumber">Pergunta {questionObj.numero}</h4>
                    <text>
                        {questionObj.descricao}:
                    </text>
                    {this.showAnswers(id)}
                </div>
            )
        })
    }

    //Mapeia as páginas das pesquisas
    mapPages = () => {
        if (this.state.isLoaded) {
            // console.log(this.state.pages[this.state.pageIndex])
            let pageObj = this.state.pages[this.state.pageIndex]
            return (
                <div>
                    <h2 className ="Title" >Página {pageObj.numero}</h2>
                    {this.showQuestions()}

                </div>
            )
        }
    }

    //Troca o índice da página
    changePageIndex = (sum) => {
        let index = this.state.pageIndex + sum
        // console.log(index)
        this.setState({
            pageIndex: index,
            answeredPage: []
        })
        document.getElementById("Form").reset();
        optVector = []
    }

    //Envia a pesquisa preenchida para o banco de dados
    storageResearch = () => {
        this.setState({ answeredPage: optVector }, () => {
            let response = api.post("/api/dados/enviar-entrevistados", this.state.answeredPage)
            //console.log(response)
            localStorage.setItem("tempResearch" + this.state.answeredPage[0].folha, JSON.stringify(this.state.answeredPage))
            alert("Sua pesquisa foi submetida")
        })
    }

    //Verifica se a página já foi preenchida anteriormente
    handleSubmit = (e) => {
        e.preventDefault()
        const response = JSON.parse(localStorage.getItem("tempResearch1"))
        const response2 = JSON.parse(localStorage.getItem("tempResearch2"))
        const response3 = JSON.parse(localStorage.getItem("tempResearch3"))
        //console.log(optVector)
        if (optVector.length > 0) {
            if (response || response2 || response3 ) {
                // console.log(response)
                if (response !== null && response[0].folha === optVector[0].folha) {
                    alert("Esta página já foi preenchida")
                }else if (response2 !== null && response2[0].folha === optVector[0].folha) {
                    alert("Esta página já foi preenchida")
                } 
                else if (response3 !== null && response3[0].folha === optVector[0].folha) {
                    alert("Esta página já foi preenchida")
                }else {
                    this.storageResearch()
                    this.handleBack()
                }
            }else {
                this.storageResearch()
                this.handleBack()
            } 
        }
    }

    //Volta para o dashboard
    handleBack = () => {
        this.props.history.goBack()
    }

    render() {

        return (
            <div className="Research">
                <button className="Back" onClick={this.handleBack}>Voltar</button>
                <form className="Form" id={"Form"} onSubmit={this.handleSubmit}>
                    <h1 className ="Title"> Pesquisa </h1>

                    {
                        this.mapPages()
                    }
                    <br />
                    <button type="submit" >Enviar</button>
                    <br />
                </form>
                <div className="pageB">
                {
                    this.state.pageIndex === 0 ?
                        <button className = "NextB"onClick={() => this.changePageIndex(1)}>Página seguinte</button>
                        : this.state.pageIndex === 1 ?
                                <div>
                                    <button className = "PrevB" onClick={() => this.changePageIndex(-1)}>Página anterior</button>
                                    <button className = "NextB" onClick={() => this.changePageIndex(1)}>Página seguinte</button>
                                </div>
                                : <button className = "PrevB" onClick={() => this.changePageIndex(-1)}>Página anterior</button>
                                
                }
                </div>


            </div>
        )
    }
}

export default Research