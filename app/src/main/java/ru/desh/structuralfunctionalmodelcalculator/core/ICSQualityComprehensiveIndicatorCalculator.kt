package ru.desh.structuralfunctionalmodelcalculator.core

import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICS
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMFunction
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMIndicator
import ru.desh.structuralfunctionalmodelcalculator.model.entity.TechnicalSystem
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class ICSQualityComprehensiveIndicatorCalculator {
    companion object {

        fun calculateQualityIndicator(icsProject: ICS){
            if (validateICS(icsProject)) {
                val baseSFMMatrices = getSFMMatrices(icsProject.baseSFM)

                for (rsfm in icsProject.realSFMs) {
                    val realSFMMatrices = getSFMMatrices(rsfm.sfmFunctions)

                    val deltaMin = subtractMatrices(realSFMMatrices.first, baseSFMMatrices.first)
                    val deltaMax = subtractMatrices(baseSFMMatrices.second, realSFMMatrices.second)

                    val indicator: Float =
                        if (deltaMin.any { it -> it.any { it < 0 } } || deltaMax.any { it -> it.any { it < 0 } })
                        { -1F } else { calculateComprehensiveQualityIndicator(deltaMin, deltaMax) }

                    rsfm.qualityComprehensiveIndicator = indicator
                }
            }
        }

        private fun validateICS(icsProject: ICS): Boolean {
            return true
        }

        private fun sortAllByPriority(sfmFunctions: List<SFMFunction>) {
            val sfmFunctionComparator = Comparator<SFMFunction> { o1, o2 ->
                when {
                    o1.functionPriority > o2.functionPriority -> return@Comparator 1
                    o1.functionPriority < o2.functionPriority -> return@Comparator -1
                    else -> return@Comparator 0
                }
            }
            Collections.sort(sfmFunctions, sfmFunctionComparator)

            val indicatorComparator = Comparator<SFMIndicator> { o1, o2 ->
                when {
                    o1.indicatorPriority > o2.indicatorPriority -> return@Comparator 1
                    o1.indicatorPriority < o2.indicatorPriority -> return@Comparator -1
                    else -> return@Comparator 0
                }
            }

            val techSystemComparator = Comparator<TechnicalSystem> { o1, o2 ->
                when {
                    o1.systemPriority > o2.systemPriority -> return@Comparator 1
                    o1.systemPriority < o2.systemPriority -> return@Comparator -1
                    else -> return@Comparator 0
                }
            }

            for (func in sfmFunctions) {
                Collections.sort(func.technicalSystems, techSystemComparator)
                for (ts in func.technicalSystems) {
                    Collections.sort(ts.systemIndicators, indicatorComparator)
                }
            }
        }

        private fun getSFMMatrices(sfmFunctions: List<SFMFunction>): Pair<MutableList<MutableList<Float>>,
                MutableList<MutableList<Float>>> {
            val minArr: MutableList<MutableList<Float>> = mutableListOf()
            val maxArr: MutableList<MutableList<Float>> = mutableListOf()
            var indY = 0

            sortAllByPriority(sfmFunctions)

            for (func in sfmFunctions) {
                for (ts in func.technicalSystems) {
                    minArr.add(mutableListOf())
                    maxArr.add(mutableListOf())
                    for ((j, si) in ts.systemIndicators.withIndex()) {
                        minArr[indY].add(si.minValue)
                        maxArr[indY].add(si.maxValue)
                    }
                    indY++
                }
            }
            return Pair(minArr, maxArr)
        }


        private fun subtractMatrices(mA: MutableList<MutableList<Float>>, mB: MutableList<MutableList<Float>>): MutableList<MutableList<Float>> {
            val mC: MutableList<MutableList<Float>> = mutableListOf()
            for (i in 0 until mA.size) {
                mC.add(mutableListOf())
                for (j in 0 until mB[0].size) {
                    mC[i].add(mA[i][j] - mB[i][j])
                }
            }
            return mC
        }

        private fun calculateComprehensiveQualityIndicator(deltaMin: MutableList<MutableList<Float>>,
                                                           deltaMax: MutableList<MutableList<Float>>): Float {
            var l = 1
            val m = (deltaMin.size).toFloat()
            val n = (deltaMax[0].size * 2).toFloat()

            val deltaQualityIndicators: MutableList<MutableList<Float>> = mutableListOf()

            var q: Float
            for (i in 0 until deltaMin.size) {
                deltaQualityIndicators.add(mutableListOf())
                for (j in 0 until deltaMax[0].size) {
                    q = (2 * (((m*n - l) + 1)/( m*n + m*n*m*n)))
                    deltaQualityIndicators[i].add(deltaMin[i][j] * q)
                    l++
                    q = (2 * (((m*n - l) + 1)/( m*n + m*n*m*n)))
                    deltaQualityIndicators[i].add(deltaMax[i][j] * q)
                    l++
                }
            }

            return matrixSum(deltaQualityIndicators)
        }

        private fun matrixSum(matrix: List<List<Float>>): Float {
            var sum = 0F
            for (row in matrix) {
                for (v in row) {
                    sum += v
                }
            }
            return sum
        }
    }
}