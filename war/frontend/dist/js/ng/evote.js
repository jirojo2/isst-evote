angular
	.module('evote', [])
	.controller('CACtrl', ['$scope', '$http', function($scope, $http) {
	
	}])
	.controller('ResultadosCtrl', ['$scope', '$http', function($scope, $http) {
	
		$scope.resultados = null;
		$scope.candidatos = [];
		$scope.sectores = [];
		
		$scope.grafico = null;
		
		window.scope = $scope;
		
		$http
			.get('/dist/js/ng/resultados.json')
			.success(function(data) {
				$scope.resultados = data;
				$scope.candidatos = data.candidatos;
				$scope.sectores = Object.keys(data.votosEmitidos);
				$scope.pintarGrafico();
			})
			
		$scope.idxCandidato = function(candidato) {
			for (var i = 0; i < $scope.candidatos.length; i++)
				if ($scope.candidatos[i].candidato === candidato)
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
		
		$scope.resultados = function() {
			// TODO: ir a globales
		}
		
		$scope.pintarGrafico = function() {
			
			if ($scope.grafico) {
				$scope.grafico.destroy();
			}
			
			var pieChartCanvas = $("#votesPieChart").get(0).getContext("2d");
			var pieChart = new Chart(pieChartCanvas);
			var PieData = $scope.candidatos.map(function(i) {
				return {
					value: i.totalPonderado * 100, 
					color: $scope.colorHexCandidato(i.candidato),
					highlight: $scope.colorHexCandidato(i.candidato),
					label: i.candidato
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
				tooltipTemplate: "<%=label%>: <%=value.toFixed(2) %>% votos"
			};
			//Create pie or douhnut chart 
			$scope.grafico = pieChart.Doughnut(PieData, pieOptions);
		}
	}])
	