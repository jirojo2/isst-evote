angular
	.module('evote', [])
	.controller('Resultados', ['$scope', '$http', function($scope, $http) {
	
		$scope.resultados = null;
		$scope.escuelas = [];
		$scope.candidatos = [];
		
		$scope.grafico = null;
		$scope.activo = 'Globales';		
		$scope.r = null;
		
		window.scope = $scope;
		
		$http
			.get('/resultados')
			.success(function(data) {
				$scope.resultados = data;
				$scope.totalVotos = 0;
				$scope.escuelas = [];
				$scope.candidatos = [];
				
				$scope.ganador = data.global.ganador;
				$scope.totalVotos = data.global.totalVotos;
				
				for (var i = 0; i < data.global.recuento.length; i++) {
					var c = data.global.recuento[i];
					$scope.candidatos.push(c.candidato);
				}

				for (var i = 0; i < data.escuelas.length; i++) {
					var c = data.escuelas[i];
					$scope.escuelas.push(c.escuela.nombre);
				}
				
				$scope.r = data.global;
				$scope.pintarGrafico();
			})
		
		$scope.porcentajeVotos = function(votos, total) {
			return votos * 100 / total;
		}
		
		$scope.idxCandidato = function(candidato) {
			for (var i = 0; i < $scope.candidatos.length; i++)
				if ($scope.candidatos[i].id.id === candidato.id.id)
					return i;
			return -1;
		}
		
		$scope.colorCandidato = function(candidato) {
			var colores = ['red', 'green', 'yellow'];
			return colores[$scope.idxCandidato(candidato) % colores.length];
		}
		
		$scope.colorHexCandidato = function(candidato) {
			var colores = ['#f56954', '#00a65a', '#f39c12'];
			return colores[$scope.idxCandidato(candidato) % colores.length];
		}
		
		$scope.resultadosEscuela = function(escuela) {
			if (!escuela) {
				$scope.activo = 'Globales';
				$scope.r = $scope.resultados.global;
				$scope.pintarGrafico();
				return;
			}
			for (var i = 0; i < $scope.resultados.escuelas.length; i++) {
				var c = $scope.resultados.escuelas[i];
				if (c.escuela.nombre === escuela) {
					$scope.r = c.recuento;
					$scope.activo = escuela;
					$scope.pintarGrafico();
					break;
				}
			}
		}
		
		$scope.pintarGrafico = function() {
			
			if ($scope.grafico) {
				$scope.grafico.destroy();
			}
			
			var pieChartCanvas = $("#votesPieChart").get(0).getContext("2d");
			var pieChart = new Chart(pieChartCanvas);
			var PieData = $scope.r.recuento.map(function(i) {
				return {
					value: $scope.porcentajeVotos(i.votos, $scope.r.totalVotos), 
					color: $scope.colorHexCandidato(i.candidato),
					highlight: $scope.colorHexCandidato(i.candidato),
					label: i.candidato.nombre + " " + i.candidato.apellidos
				}
			});
			var pieOptions = {
				//Boolean - Whether we should show a stroke on each segment
				segmentShowStroke: true,
				//String - The colour of each segment stroke
				segmentStrokeColor: "#fff",
				//Number - The width of each segment stroke
				segmentStrokeWidth: 1,
				//Number - The percentage of the chart that we cut out of the middle
				percentageInnerCutout: 50, // This is 0 for Pie charts
				//Number - Amount of animation steps
				animationSteps: 100,
				//String - Animation easing effect
				animationEasing: "easeOutBounce",
				//Boolean - Whether we animate the rotation of the Doughnut
				animateRotate: true,
				//Boolean - Whether we animate scaling the Doughnut from the centre
				animateScale: false,
				//Boolean - whether to make the chart responsive to window resizing
				responsive: true,
				//Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
				maintainAspectRatio: false,
				//String - A legend template
				legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<segments.length; i++){%><li><span style=\"background-color:<%=segments[i].fillColor%>\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>",
				//String - A tooltip template
				tooltipTemplate: "<%=label%>: <%=value %>% votos"
			};
			//Create pie or douhnut chart 
			$scope.grafico = pieChart.Doughnut(PieData, pieOptions);
		}
	}])
	